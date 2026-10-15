package br.com.pdf_storage_worker.application;

import br.com.pdf_storage_worker.api.CreateTermListenerDTO;
import br.com.pdf_storage_worker.api.HadwareListenerDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TermListener {

    private String obterCaminhoBaseUploads() {
        // Pega o diretório de execução atual do Worker no HD
        String caminhoExecucao = System.getProperty("user.dir");
        Path path = Paths.get(caminhoExecucao);

        // Se o processo iniciar dentro da subpasta do módulo, subimos até encontrar a
        // raiz 'termo'
        if (path.toString().endsWith("pdf-storage-worker")) {
            path = path.getParent(); // Sobe para 'termo'
        }

        // Concatena com o caminho exato onde a pasta 'uploads' está de verdade
        return path.resolve("uploads").resolve("assinaturas").toString();
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void generateTermPdf(@Payload CreateTermListenerDTO listenerDTO) {
        try {
            System.out.println("Mensagem recebida do RabbitMQ: " + listenerDTO);
            generatePdfTerm(listenerDTO);
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem do RabbitMQ e gerar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void generatePdfTerm(CreateTermListenerDTO dados) throws Exception {
        String uuidPasta = dados.id();

        // Resolve o caminho dinamicamente usando a nova lógica segura
        String caminhoBase = obterCaminhoBaseUploads();
        Path pastaTermoPath = Paths.get(caminhoBase, uuidPasta).toAbsolutePath().normalize();
        File pastaDestino = pastaTermoPath.toFile();

        // Aguarda a gravação no disco caso o outro serviço ainda esteja escrevendo
        int tentativas = 0;
        int maxTentativas = 6;
        while (!pastaDestino.exists() && tentativas < maxTentativas) {
            tentativas++;
            System.out.println("Aguardando criação da pasta no caminho dinâmico (Tentativa " + tentativas + "/"
                    + maxTentativas + ")...");
            Thread.sleep(1500);
        }

        if (!pastaDestino.exists()) {
            System.err.println(
                    "ERRO CRÍTICO: Pasta não encontrada no caminho resolvido: " + pastaDestino.getAbsolutePath());
            throw new FileNotFoundException(
                    "Diretório de assinaturas não encontrado: " + pastaDestino.getAbsolutePath());
        }

        // Se encontrou a pasta, monta o PDF
        String caminhoDestinoPdf = pastaTermoPath.resolve("termo_recebimento.pdf").toString();
        Document document = new Document(PageSize.A4, 45, 45, 45, 45);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(caminhoDestinoPdf));
            document.open();

            // Fontes básicas do documento
            Font fonteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fonteTexto = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fonteNegrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // 1. TÍTULO
            Paragraph titulo = new Paragraph("TERMO DE RESPONSABILIDADE E RECEBIMENTO DE EQUIPAMENTOS", fonteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(25);
            document.add(titulo);

            // 2. DADOS DO COLABORADOR
            PdfPTable tabelaDados = new PdfPTable(2);
            tabelaDados.setWidthPercentage(100);
            tabelaDados.setSpacingAfter(15);
            tabelaDados.addCell(criarCelula("Colaborador:", dados.employeeName(), fonteNegrito, fonteTexto));
            tabelaDados.addCell(criarCelula("Setor:", dados.department(), fonteNegrito, fonteTexto));
            document.add(tabelaDados);

            // 3. TEXTO DESCRITIVO DO CONTRATO
            Paragraph texto = new Paragraph();
            texto.setFont(fonteTexto);
            texto.setLeading(14);
            texto.add("Declaro ter recebido os equipamentos descritos abaixo em perfeitas condições de uso, " +
                    "assumindo total responsabilidade pela guarda e conservação dos mesmos.\n\n");
            texto.setSpacingAfter(10);
            document.add(texto);

            // --- SEÇÃO ATUALIZADA: TABELA DE EQUIPAMENTOS ESTILIZADA ---
            // Define proporções das colunas: Descrição (peso 4), Quantidade (peso 1),
            // Patrimônio (peso 2)
            PdfPTable tabelaEquipamentos = new PdfPTable(new float[] { 4f, 1f, 2f });
            tabelaEquipamentos.setWidthPercentage(100);
            tabelaEquipamentos.setSpacingAfter(30);

            // Cores e fontes dedicadas à tabela corporativa
            Font fonteCabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.NORMAL,
                    java.awt.Color.WHITE);
            Font fonteLinhaTabela = FontFactory.getFont(FontFactory.HELVETICA, 9);
            java.awt.Color corCabecalho = new java.awt.Color(60, 60, 60); // Cinza Escuro

            // Criando e adicionando células de cabeçalho
            PdfPCell h1 = new PdfPCell(new Paragraph("Equipamento / Descrição", fonteCabecalho));
            h1.setBackgroundColor(corCabecalho);
            h1.setPadding(6);

            PdfPCell h2 = new PdfPCell(new Paragraph("Qtd", fonteCabecalho));
            h2.setBackgroundColor(corCabecalho);
            h2.setHorizontalAlignment(Element.ALIGN_CENTER);
            h2.setPadding(6);

            PdfPCell h3 = new PdfPCell(new Paragraph("Patrimônio (Asset Tag)", fonteCabecalho));
            h3.setBackgroundColor(corCabecalho);
            h3.setHorizontalAlignment(Element.ALIGN_CENTER);
            h3.setPadding(6);

            tabelaEquipamentos.addCell(h1);
            tabelaEquipamentos.addCell(h2);
            tabelaEquipamentos.addCell(h3);

            // Varre dinamicamente a lista contida dentro do seu Record DTO
            if (dados.hardwares() != null) {
                int contadorLinha = 0;
                for (HadwareListenerDTO item : dados.hardwares()) {
                    // Efeito zebrado: Linhas alternadas entre Branco e Cinza Claro
                    java.awt.Color corLinha = (contadorLinha % 2 == 0) ? java.awt.Color.WHITE
                            : new java.awt.Color(245, 245, 245);

                    // Célula Descrição (Usa o método mapeado do seu DTO interno)
                    PdfPCell cDesc = new PdfPCell(new Paragraph(item.description(), fonteLinhaTabela));
                    cDesc.setBackgroundColor(corLinha);
                    cDesc.setPadding(6);
                    tabelaEquipamentos.addCell(cDesc);

                    // Célula Quantidade
                    PdfPCell cQtd = new PdfPCell(new Paragraph(String.valueOf(item.qty()), fonteLinhaTabela));
                    cQtd.setBackgroundColor(corLinha);
                    cQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cQtd.setPadding(6);
                    tabelaEquipamentos.addCell(cQtd);

                    // Célula Patrimônio (Trata o valor '0' ou nulo de forma limpa)
                    String tagTexto = (item.assetTag() == 0 || "0".equals(String.valueOf(item.assetTag()))) ? "-"
                            : String.valueOf(item.assetTag());
                    PdfPCell cTag = new PdfPCell(new Paragraph(tagTexto, fonteLinhaTabela));
                    cTag.setBackgroundColor(corLinha);
                    cTag.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cTag.setPadding(6);
                    tabelaEquipamentos.addCell(cTag);

                    contadorLinha++;
                }
            }
            document.add(tabelaEquipamentos);
            // -----------------------------------------------------------

            // 4. DATA
            String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            Paragraph dataParagrafo = new Paragraph("Data de Emissão: " + dataAtual, fonteTexto);
            dataParagrafo.setSpacingAfter(40);
            document.add(dataParagrafo);

            // 5. TABELA DE ASSINATURAS
            PdfPTable tabelaAssinaturas = new PdfPTable(2);
            tabelaAssinaturas.setWidthPercentage(100);

            String caminhoAssinaturaTI = pastaTermoPath.resolve("responsible-it-signature.png").toString();
            String caminhoAssinaturaColab = pastaTermoPath.resolve("employee-signature.png").toString();

            // --- COLUNA 1: RESPONSÁVEL DA TI ---
            PdfPCell celulaTI = new PdfPCell();
            celulaTI.setBorder(Rectangle.NO_BORDER);
            celulaTI.setHorizontalAlignment(Element.ALIGN_CENTER);

            File arquivoTI = new File(caminhoAssinaturaTI);
            if (arquivoTI.exists()) {
                Image imgTI = Image.getInstance(arquivoTI.getAbsolutePath());
                imgTI.scaleToFit(160, 65);
                imgTI.setAlignment(Element.ALIGN_CENTER);
                celulaTI.addElement(imgTI);
            } else {
                celulaTI.addElement(new Paragraph("\n\n\n"));
            }
            Paragraph textoTI = new Paragraph("_____________________________________\nAssinatura do Analista (TI)",
                    fonteTexto);
            textoTI.setAlignment(Element.ALIGN_CENTER);
            celulaTI.addElement(textoTI);

            // --- COLUNA 2: COLABORADOR ---
            PdfPCell celulaColab = new PdfPCell();
            celulaColab.setBorder(Rectangle.NO_BORDER);
            celulaColab.setHorizontalAlignment(Element.ALIGN_CENTER);

            File arquivoColab = new File(caminhoAssinaturaColab);
            if (arquivoColab.exists()) {
                Image imgColab = Image.getInstance(arquivoColab.getAbsolutePath());
                imgColab.scaleToFit(160, 65);
                imgColab.setAlignment(Element.ALIGN_CENTER);
                celulaColab.addElement(imgColab);
            } else {
                celulaColab.addElement(new Paragraph("\n\n\n"));
            }
            Paragraph textoColab = new Paragraph(
                    "_____________________________________\nAssinatura do Colaborador\n" + dados.employeeName(),
                    fonteTexto);
            textoColab.setAlignment(Element.ALIGN_CENTER);
            celulaColab.addElement(textoColab);

            tabelaAssinaturas.addCell(celulaTI);
            tabelaAssinaturas.addCell(celulaColab);
            document.add(tabelaAssinaturas);

            System.out.println("PDF gerado com sucesso para o UUID: " + uuidPasta);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    private PdfPCell criarCelula(String label, String valor, Font fLabel, Font fValor) {
        Phrase p = new Phrase();
        p.add(new Chunk(label + " ", fLabel));
        p.add(new Chunk(valor, fValor));
        PdfPCell cell = new PdfPCell(p);
        cell.setPadding(8);
        cell.setBorder(Rectangle.BOX);
        return cell;
    }
}
