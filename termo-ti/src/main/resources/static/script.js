// ======================================================
// CONFIGURAÇÃO DA API
// ======================================================

const API_URL = "/api/v1";


// ======================================================
// ELEMENTOS
// ======================================================

const formTermo = document.getElementById("formTermo");

const nomeInput = document.getElementById("nome");
const setorInput = document.getElementById("setor");

const dataRecebimento =
    document.getElementById("dataRecebimento");

const listaEquipamentos =
    document.getElementById("listaEquipamentos");

const listaVazia =
    document.getElementById("listaVazia");

const contadorTexto =
    document.getElementById("contadorTexto");

const btnAbrirModalEquipamento =
    document.getElementById("btnAbrirModalEquipamento");

const btnConfirmarEquipamento =
    document.getElementById("btnConfirmarEquipamento");

const descricaoEquipamento =
    document.getElementById("descricaoEquipamento");

const quantidadeEquipamento =
    document.getElementById("quantidadeEquipamento");

const patrimonioEquipamento =
    document.getElementById("patrimonioEquipamento");


// ======================================================
// MODAL BOOTSTRAP
// ======================================================

const modalElement =
    document.getElementById("modalEquipamento");

const modalEquipamento =
    new bootstrap.Modal(modalElement);


// ======================================================
// ARRAY DE EQUIPAMENTOS
// ======================================================

let equipamentos = [];


// ======================================================
// ID DO TERMO CRIADO
// ======================================================

let termoId = null;


// ======================================================
// DATA ATUAL
// ======================================================

function carregarData() {

    const agora = new Date();

    const dia =
        String(agora.getDate()).padStart(2, "0");

    const mes =
        String(agora.getMonth() + 1).padStart(2, "0");

    const ano =
        agora.getFullYear();

    dataRecebimento.textContent =
        `${dia}/${mes}/${ano}`;
}

carregarData();


// ======================================================
// ABRIR MODAL
// ======================================================

btnAbrirModalEquipamento.addEventListener(
    "click",
    function () {

        descricaoEquipamento.value = "";
        quantidadeEquipamento.value = "1";
        patrimonioEquipamento.value = "";

        modalEquipamento.show();

        setTimeout(() => {
            descricaoEquipamento.focus();
        }, 300);
    }
);


// ======================================================
// ADICIONAR EQUIPAMENTO
// ======================================================

btnConfirmarEquipamento.addEventListener(
    "click",
    function () {

        const descricao =
            descricaoEquipamento.value.trim();

        const quantidade =
            Number(quantidadeEquipamento.value);

        /*
         * Patrimônio NÃO é obrigatório.
         *
         * Se ficar vazio:
         *
         * assetTag = ""
         *
         * A API será responsável por transformar
         * o valor vazio em "0000".
         */
        const patrimonio =
            patrimonioEquipamento.value.trim();


        // ----------------------------------------------
        // VALIDAÇÃO DA DESCRIÇÃO
        // ----------------------------------------------

        if (!descricao) {

            alert(
                "Informe a descrição do equipamento."
            );

            descricaoEquipamento.focus();

            return;
        }


        // ----------------------------------------------
        // VALIDAÇÃO DA QUANTIDADE
        // ----------------------------------------------

        if (!quantidade || quantidade < 1) {

            alert(
                "Informe uma quantidade válida."
            );

            quantidadeEquipamento.focus();

            return;
        }


        // ----------------------------------------------
        // ADICIONA NO ARRAY
        // ----------------------------------------------

        equipamentos.push({

            description: descricao,

            qty: quantidade,

            assetTag: patrimonio

        });


        // ----------------------------------------------
        // ATUALIZA A TABELA
        // ----------------------------------------------

        atualizarTabelaEquipamentos();


        // ----------------------------------------------
        // FECHA MODAL
        // ----------------------------------------------

        modalEquipamento.hide();
    }
);


// ======================================================
// ATUALIZAR TABELA
// ======================================================

function atualizarTabelaEquipamentos() {

    listaEquipamentos.innerHTML = "";


    // ----------------------------------------------
    // ESTADO VAZIO
    // ----------------------------------------------

    if (equipamentos.length === 0) {

        listaVazia.style.display = "block";

        contadorTexto.textContent =
            "Nenhum equipamento";

        return;
    }


    // ----------------------------------------------
    // ESCONDE ESTADO VAZIO
    // ----------------------------------------------

    listaVazia.style.display = "none";


    // ----------------------------------------------
    // CONTADOR
    // ----------------------------------------------

    const totalQuantidade =
        equipamentos.reduce(
            (total, equipamento) =>
                total + equipamento.qty,
            0
        );

    contadorTexto.textContent =
        totalQuantidade === 1
            ? "1 equipamento"
            : `${totalQuantidade} equipamentos`;


    // ----------------------------------------------
    // MONTA TABELA
    // ----------------------------------------------

    equipamentos.forEach(
        (equipamento, index) => {

            const tr =
                document.createElement("tr");


            // DESCRIÇÃO

            const tdDescricao =
                document.createElement("td");

            tdDescricao.textContent =
                equipamento.description;


            // QUANTIDADE

            const tdQuantidade =
                document.createElement("td");

            tdQuantidade.textContent =
                equipamento.qty;


            // PATRIMÔNIO

            const tdPatrimonio =
                document.createElement("td");

            /*
             * Se estiver vazio, mostramos 0000 na tela
             * somente como representação.
             *
             * O valor enviado para a API continua sendo
             * o valor original vazio.
             */
            tdPatrimonio.textContent =
                equipamento.assetTag || "0000";


            // AÇÃO

            const tdAcao =
                document.createElement("td");

            tdAcao.className =
                "text-center";


            const btnRemover =
                document.createElement("button");

            btnRemover.type = "button";

            btnRemover.className =
                "btn btn-sm btn-outline-danger";

            btnRemover.textContent =
                "Remover";


            btnRemover.addEventListener(
                "click",
                function () {

                    equipamentos.splice(index, 1);

                    atualizarTabelaEquipamentos();
                }
            );


            tdAcao.appendChild(btnRemover);


            // MONTA LINHA

            tr.appendChild(tdDescricao);
            tr.appendChild(tdQuantidade);
            tr.appendChild(tdPatrimonio);
            tr.appendChild(tdAcao);

            listaEquipamentos.appendChild(tr);
        }
    );
}


// ======================================================
// ASSINATURAS
// ======================================================

const canvasColaborador =
    document.getElementById(
        "assinaturaColaborador"
    );

const canvasResponsavel =
    document.getElementById(
        "assinaturaResponsavel"
    );


const ctxColaborador =
    canvasColaborador.getContext("2d");

const ctxResponsavel =
    canvasResponsavel.getContext("2d");


// ======================================================
// CONFIGURAR CANVAS
// ======================================================

function configurarAssinatura(canvas, ctx) {

    let desenhando = false;


    ctx.lineWidth = 2;
    ctx.lineCap = "round";
    ctx.lineJoin = "round";
    ctx.strokeStyle = "#000";


    function obterPosicao(event) {

        const rect =
            canvas.getBoundingClientRect();


        if (
            event.touches &&
            event.touches.length > 0
        ) {

            return {

                x:
                    event.touches[0].clientX -
                    rect.left,

                y:
                    event.touches[0].clientY -
                    rect.top

            };
        }


        return {

            x:
                event.clientX -
                rect.left,

            y:
                event.clientY -
                rect.top

        };
    }


    function iniciar(event) {

        event.preventDefault();

        desenhando = true;

        const posicao =
            obterPosicao(event);

        ctx.beginPath();

        ctx.moveTo(
            posicao.x,
            posicao.y
        );
    }


    function desenhar(event) {

        if (!desenhando) {
            return;
        }

        event.preventDefault();

        const posicao =
            obterPosicao(event);

        ctx.lineTo(
            posicao.x,
            posicao.y
        );

        ctx.stroke();
    }


    function finalizar(event) {

        if (!desenhando) {
            return;
        }

        event.preventDefault();

        desenhando = false;

        ctx.closePath();
    }


    // MOUSE

    canvas.addEventListener(
        "mousedown",
        iniciar
    );

    canvas.addEventListener(
        "mousemove",
        desenhar
    );

    canvas.addEventListener(
        "mouseup",
        finalizar
    );

    canvas.addEventListener(
        "mouseleave",
        finalizar
    );


    // TOUCH

    canvas.addEventListener(
        "touchstart",
        iniciar,
        {
            passive: false
        }
    );

    canvas.addEventListener(
        "touchmove",
        desenhar,
        {
            passive: false
        }
    );

    canvas.addEventListener(
        "touchend",
        finalizar,
        {
            passive: false
        }
    );
}


configurarAssinatura(
    canvasColaborador,
    ctxColaborador
);

configurarAssinatura(
    canvasResponsavel,
    ctxResponsavel
);


// ======================================================
// LIMPAR ASSINATURA
// ======================================================

document
    .querySelectorAll(".btn-limpar")
    .forEach(
        botao => {

            botao.addEventListener(
                "click",
                function () {

                    const canvasId =
                        botao.dataset.canvas;

                    const canvas =
                        document.getElementById(
                            canvasId
                        );

                    const ctx =
                        canvas.getContext("2d");

                    ctx.clearRect(
                        0,
                        0,
                        canvas.width,
                        canvas.height
                    );
                }
            );
        }
    );


// ======================================================
// VERIFICAR ASSINATURA
// ======================================================

function possuiAssinatura(canvas) {

    const ctx =
        canvas.getContext("2d");

    const imagem =
        ctx.getImageData(
            0,
            0,
            canvas.width,
            canvas.height
        );


    for (
        let i = 3;
        i < imagem.data.length;
        i += 4
    ) {

        if (imagem.data[i] !== 0) {
            return true;
        }
    }

    return false;
}


// ======================================================
// CANVAS → BLOB
// ======================================================

function canvasParaBlob(canvas) {

    return new Promise(
        (resolve, reject) => {

            canvas.toBlob(
                blob => {

                    if (!blob) {

                        reject(
                            new Error(
                                "Não foi possível gerar a assinatura."
                            )
                        );

                        return;
                    }

                    resolve(blob);
                },
                "image/png"
            );
        }
    );
}


// ======================================================
// CRIAR TERMO
// ======================================================

async function criarTermo() {

    const employeeName =
        nomeInput.value.trim();

    const department =
        setorInput.value.trim();


    // ----------------------------------------------
    // VALIDAÇÕES
    // ----------------------------------------------

    if (!employeeName) {

        alert(
            "Informe o nome do colaborador."
        );

        nomeInput.focus();

        return null;
    }


    if (!department) {

        alert(
            "Informe o setor."
        );

        setorInput.focus();

        return null;
    }


    if (equipamentos.length === 0) {

        alert(
            "Adicione pelo menos um equipamento."
        );

        return null;
    }


    // ----------------------------------------------
    // JSON EXATAMENTE NO FORMATO DA API
    // ----------------------------------------------

    const payload = {

        employeeName: employeeName,

        department: department,

        hardwares: equipamentos.map(
            equipamento => ({

                description:
                    equipamento.description,

                qty:
                    equipamento.qty,

                /*
                 * IMPORTANTE:
                 *
                 * Não colocamos "0000" aqui.
                 *
                 * Se o usuário deixar vazio:
                 *
                 * assetTag = ""
                 *
                 * A API faz a validação e transforma
                 * em "0000".
                 */
                assetTag:
                    equipamento.assetTag
            })
        )
    };


    console.log(
        "JSON enviado para a API:"
    );

    console.log(
        JSON.stringify(
            payload,
            null,
            2
        )
    );


    // ----------------------------------------------
    // POST /api/v1/terms
    // ----------------------------------------------

    const response =
        await fetch(
            `${API_URL}/terms`,
            {

                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body:
                    JSON.stringify(payload)
            }
        );


    // ----------------------------------------------
    // TRATAMENTO DE ERRO
    // ----------------------------------------------

    if (!response.ok) {

        let mensagem =
            "Erro ao criar o termo.";

        try {

            const erro =
                await response.json();

            mensagem =
                erro.message ||
                erro.detail ||
                erro.title ||
                mensagem;

        } catch (_) {
            // resposta não era JSON
        }


        throw new Error(mensagem);
    }


    // ----------------------------------------------
    // RESPOSTA
    // ----------------------------------------------

    const termo =
        await response.json();


    console.log(
        "Termo criado:"
    );

    console.log(termo);


    // ----------------------------------------------
    // UUID
    // ----------------------------------------------

    termoId =
        termo.id;


    if (!termoId) {

        throw new Error(
            "A API criou o termo, mas não retornou o ID."
        );
    }


    return termo;
}


// ======================================================
// ENVIAR ASSINATURAS
// ======================================================

async function enviarAssinaturas(id) {

    // ----------------------------------------------
    // VERIFICA ASSINATURA COLABORADOR
    // ----------------------------------------------

    if (
        !possuiAssinatura(
            canvasColaborador
        )
    ) {

        throw new Error(
            "A assinatura do colaborador é obrigatória."
        );
    }


    // ----------------------------------------------
    // VERIFICA ASSINATURA RESPONSÁVEL
    // ----------------------------------------------

    if (
        !possuiAssinatura(
            canvasResponsavel
        )
    ) {

        throw new Error(
            "A assinatura do responsável de TI é obrigatória."
        );
    }


    // ----------------------------------------------
    // TRANSFORMA EM PNG
    // ----------------------------------------------

    const employeeSignature =
        await canvasParaBlob(
            canvasColaborador
        );


    const responsibleItSignature =
        await canvasParaBlob(
            canvasResponsavel
        );


    // ----------------------------------------------
    // FORMDATA
    // ----------------------------------------------

    const formData =
        new FormData();


    formData.append(
        "employeeSignature",
        employeeSignature,
        "employee-signature.png"
    );


    formData.append(
        "responsibleItSignature",
        responsibleItSignature,
        "responsible-it-signature.png"
    );


    console.log(
        "Enviando assinaturas..."
    );


    // ----------------------------------------------
    // POST /terms/{id}/signatures
    // ----------------------------------------------

    const response =
        await fetch(
            `${API_URL}/terms/${id}/signatures`,
            {

                method: "POST",

                /*
                 * NÃO colocar Content-Type aqui.
                 *
                 * O navegador precisa gerar automaticamente:
                 *
                 * multipart/form-data;
                 * boundary=...
                 */

                body:
                    formData
            }
        );


    // ----------------------------------------------
    // ERRO
    // ----------------------------------------------

    if (!response.ok) {

        let mensagem =
            "Erro ao enviar as assinaturas.";

        try {

            const erro =
                await response.json();

            mensagem =
                erro.message ||
                erro.detail ||
                erro.title ||
                mensagem;

        } catch (_) {
            // resposta não era JSON
        }


        throw new Error(mensagem);
    }


    // ----------------------------------------------
    // RESPOSTA
    // ----------------------------------------------

    return await response
        .json()
        .catch(() => null);
}


// ======================================================
// SUBMIT DO TERMO
// ======================================================

formTermo.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const btnSalvar =
            document.getElementById(
                "btnSalvar"
            );


        try {

            // ------------------------------------------
            // DESABILITA BOTÃO
            // ------------------------------------------

            btnSalvar.disabled = true;

            btnSalvar.textContent =
                "Salvando...";


            // ------------------------------------------
            // 1. CRIA TERMO
            // ------------------------------------------

            const termo =
                await criarTermo();


            if (!termo || !termoId) {
                return;
            }


            // ------------------------------------------
            // 2. ENVIA ASSINATURAS
            // ------------------------------------------

            await enviarAssinaturas(
                termoId
            );


            // ------------------------------------------
            // SUCESSO
            // ------------------------------------------

            alert(
                "Termo criado e assinado com sucesso!"
            );


            console.log(
                "Termo finalizado:",
                termoId
            );


            // ------------------------------------------
            // LIMPA FORMULÁRIO
            // ------------------------------------------

            formTermo.reset();

            equipamentos = [];

            termoId = null;

            atualizarTabelaEquipamentos();


            // ------------------------------------------
            // LIMPA ASSINATURAS
            // ------------------------------------------

            ctxColaborador.clearRect(
                0,
                0,
                canvasColaborador.width,
                canvasColaborador.height
            );


            ctxResponsavel.clearRect(
                0,
                0,
                canvasResponsavel.width,
                canvasResponsavel.height
            );


            // ------------------------------------------
            // DATA
            // ------------------------------------------

            carregarData();


        } catch (error) {

            console.error(
                "Erro:",
                error
            );


            alert(
                error.message ||
                "Ocorreu um erro ao comunicar com a API."
            );


        } finally {

            btnSalvar.disabled = false;

            btnSalvar.textContent =
                "Salvar e Finalizar Termo";
        }
    }
);


// ======================================================
// ESTADO INICIAL
// ======================================================

atualizarTabelaEquipamentos();