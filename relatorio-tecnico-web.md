# Relatório Técnico — Aplicação Web (Spring MVC + Thymeleaf)

## Clínica Dentária — Portal da Rececionista e do Paciente

---

# Índice

## 10.2 Web — Sistema de Gestão de Clínica Dentária

### 10.2.1 Protótipo de Alta Fidelidade — Páginas Web

- 10.2.1.1 Página de Login (`/login`)
- 10.2.1.2 Página de Registo de Utilizador/Paciente (`/cadastro`)
- 10.2.1.3 Página de Recuperação de Palavra-Passe (`/recuperar-senha`)
- 10.2.1.4 Página de Redefinição de Palavra-Passe (`/redefinir-senha`)
- 10.2.1.5 Página Principal — Consultas/Agenda (`/consultas`)
- 10.2.1.6 Página de Marcação de Consulta (`/marcar-consulta`)
- 10.2.1.7 Página de Reagendamento de Consulta (`/consultas/{id}/reagendar`)
- 10.2.1.8 Página de Cancelamento de Consulta (`/consultas/{id}/cancelar`)
- 10.2.1.9 Página de Perfil do Utilizador (`/perfil`)
- 10.2.1.10 Página de Faturas — Visão Geral (`/faturas`)
- 10.2.1.11 Página de Faturas — Visão Paciente (`/paciente/faturas`)

### 10.2.2 Controllers (Spring MVC)

- 10.2.2.1 `LoginController`
- 10.2.2.2 `CadastroController`
- 10.2.2.3 `ConsultasController`
- 10.2.2.4 `MarcarConsultaController`
- 10.2.2.5 `PerfilController`
- 10.2.2.6 `FaturasController`
- 10.2.2.7 `FaturaDownloadController`
- 10.2.2.8 `RecuperacaoSenhaController`
- 10.2.2.9 `CodigoPostalController` (REST)

### 10.2.3 Templates Thymeleaf

- 10.2.3.1 `login/index.html` — Página de Login
- 10.2.3.2 `cadastro/index.html` — Página de Registo
- 10.2.3.3 `recuperar-senha/index.html` — Pedido de Recuperação
- 10.2.3.4 `redefinir-senha/index.html` — Redefinição de Palavra-Passe
- 10.2.3.5 `consultas/index.html` — Agenda/Consultas
- 10.2.3.6 `marcar-consulta/index.html` — Nova Marcação
- 10.2.3.7 `reagendar-consulta/index.html` — Reagendamento
- 10.2.3.8 `cancelar-consulta/index.html` — Cancelamento
- 10.2.3.9 `perfil/index.html` — Perfil do Utilizador
- 10.2.3.10 `faturas/index.html` — Faturas (staff)
- 10.2.3.11 `paciente/faturas.html` — Faturas (paciente)
- 10.2.3.12 `fragments/modais-legais.html` — Fragmentos Legais

### 10.2.4 Estilização e Design System

- 10.2.4.1 Paleta de Cores e Identidade Visual
- 10.2.4.2 Tipografia
- 10.2.4.3 Componentes Reutilizáveis
- 10.2.4.4 Responsividade e Breakpoints
- 10.2.4.5 Estados e Feedback Visual

### 10.2.5 Infraestrutura e Configuração

- 10.2.5.1 Estrutura e Organização do Projeto
- 10.2.5.2 Roteamento e Mapeamento de URLs
- 10.2.5.3 Gestão de Sessão e Autenticação
- 10.2.5.4 Integração entre Camadas (Controller → Service → Repository)
- 10.2.5.5 Configuração do Ambiente e Build (Gradle / application.properties)
- 10.2.5.6 Proteção CSRF e Segurança
- 10.2.5.7 Tratamento de Exceções e Validações

---

# Desenvolvimento

## 10.2.1 Protótipo de Alta Fidelidade — Páginas Web

### 10.2.1.1 Página de Login (`/login`)

**Descrição geral e objetivo no fluxo**

A página de Login é o ponto de entrada único para todos os utilizadores do sistema. Permite autenticar rececionistas, dentistas, assistentes, administradores e pacientes. Após autenticação bem-sucedida, o utilizador é redirecionado para a página principal de Consultas (`/consultas`). A página deteta sessões ativas e redireciona automaticamente, prevenindo dupla autenticação.

**Layout e estrutura visual**

A página utiliza um layout de dois painéis horizontais:
- **Painel esquerdo (cerca de 50%):** secção visual com logótipo da clínica, nome "Clínica Dentária" e slogan "Sorrisos saudáveis, vidas felizes". Fundo com gradiente verde-claro e uma ilustração decorativa SVG de dente estilizado a 40% de opacidade.
- **Painel direito (cerca de 50%):** formulário de autenticação centralizado verticalmente, com título "Aceder à Plataforma" e subtítulo "Introduza as suas credenciais para aceder ao sistema".

**Elementos de UI presentes**

- Campo `email` (tipo email, obrigatório) com rótulo "E-mail *"
- Campo `password` (tipo password, obrigatório) com rótulo "Palavra-passe *"
- Mensagem de erro de autenticação (`erroLogin`) exibida condicionalmente acima do formulário
- Mensagem de sucesso de registo (`param.cadastro`) exibida condicionalmente após criação de conta
- Mensagem de sucesso de redefinição de palavra-passe (`param.redefinida`) exibida condicionalmente
- Botão "Entrar" com tipo submit
- Hiperligação "Recuperar Palavra-passe" que aponta para `/recuperar-senha`
- Hiperligação "Criar Conta" que aponta para `/cadastro`
- Logótipo da clínica (SVG inline) no topo do formulário
- Valor do email preservado no campo após tentativa falhada (`emailInformado`)

**Comportamentos interativos**

- Submissão do formulário via método POST para `/login`
- Se o `HttpSession` já contiver `utilizadorId`, o `LoginController` redireciona automaticamente para `/consultas` sem apresentar o formulário
- Após autenticação falhada, a página recarrega com a mensagem de erro e o email preenchido
- Proteção CSRF ativa através de campo oculto injetado pelo Thymeleaf
- Navegação por hiperligação para recuperação de senha e registo

**Fluxos de navegação**

- `POST /login` (sucesso) → redirecionamento para `/consultas`
- `POST /login` (falha) → permanece em `login/index` com `erroLogin` no modelo
- `GET /login` (com sessão ativa) → redirecionamento para `/consultas`
- Hiperligação "Recuperar Palavra-passe" → `/recuperar-senha`
- Hiperligação "Criar Conta" → `/cadastro`

**Considerações de responsividade**

Nos ecrãs de ambiente de trabalho (≥1024px), os dois painéis são apresentados lado a lado. Em tablets (768px–1023px), o painel esquerdo reduz de largura. Em telemóveis (<768px), o painel esquerdo é ocultado e apenas o formulário é exibido. O formulário ocupa 100% da largura disponível com padding adequado.

---

### 10.2.1.2 Página de Registo de Utilizador/Paciente (`/cadastro`)

**Descrição geral e objetivo no fluxo**

A página de Registo permite a criação autónoma de uma nova conta de paciente. É o ponto de auto-registo para pacientes que pretendem aceder à plataforma para marcar, consultar e gerir as suas consultas. A página não é utilizada pela rececionista para registo administrado — o fluxo de registo pela rececionista é feito através da criação de conta nos bastidores.

**Layout e estrutura visual**

Layout de duas colunas assimétricas:
- **Coluna esquerda (~40%):** painel informativo com gradiente verde, logótipo, nome da clínica, texto de boas-vindas ("Junte-se à nossa clínica") e indicação de que o paciente terá acesso a marcações online, histórico e faturas. Inclui hiperligação "Já tem conta? Iniciar Sessão".
- **Coluna direita (~60%):** formulário de registo com efeito "glass" (fundo semitransparente com desfoque) sobre um fundo mais claro.

**Elementos de UI presentes**

- Campo `nome` (texto, obrigatório) — "Nome Completo *", valida mínimo de 2 palavras
- Campo `email` (email, obrigatório) — "E-mail *"
- Campo `telefone` (tel, opcional) — "Telemóvel"
- Campo `password` (password, obrigatório) — "Palavra-passe *"
- Campo `confirmPassword` (password, obrigatório) — "Confirmar Palavra-passe *"
- Checkbox `termos` (obrigatório ser true) — "Aceito os Termos e Condições e a Política de Privacidade"
- Hiperligações para abrir modais de Termos e Condições e Política de Privacidade (modais inline, não fragmentos)
- Mensagens de erro de validação individuais por campo
- Mensagens de erro globais para validações cross-campo (ex: palavras-passe não coincidem)
- Classe `campo-erro` aplicada dinamicamente a campos com erros (`th:classappend`)

**Comportamentos interativos**

- Validação server-side via Bean Validation (`@Valid`) no `CadastroForm`
- Validações do lado do servidor incluem: `@NotBlank`, `@Email`, `@Pattern` no nome (mínimo 2 palavras), `@ValidPassword` (complexidade da palavra-passe), `@ValidTelefonePortugues` (opcional), `@AssertTrue` nos termos
- Validação customizada `@PasswordMatches` que verifica se `password` === `confirmPassword`
- Submissão via POST para `/cadastro`
- Em caso de erro, o formulário preserva todos os valores preenchidos
- Modais de Termos e Privacidade são páginas internas com JavaScript inline (não fragmentos reutilizáveis)
- Após sucesso, redireciona para `/login?cadastro=sucesso`

**Fluxos de navegação**

- `POST /cadastro` (sucesso) → redirecionamento para `/login?cadastro=sucesso`
- `POST /cadastro` (erro de validação) → permanece em `cadastro/index` com erros
- Hiperligação "Iniciar Sessão" → `/login`

**Considerações de responsividade**

Em ecrãs grandes, as duas colunas são apresentadas lado a lado. Em tablets e telemóveis, a coluna esquerda é movida para o topo como secção de boas-vindas simplificada e o formulário ocupa a largura total. O efeito glass adapta o desfoque para dispositivos com menos capacidade gráfica.

---

### 10.2.1.3 Página de Recuperação de Palavra-Passe (`/recuperar-senha`)

**Descrição geral e objetivo no fluxo**

Página que permite ao utilizador solicitar um email de redefinição de palavra-passe. Faz parte do fluxo de recuperação de acesso, não exigindo autenticação prévia.

**Layout e estrutura visual**

Layout centrado de coluna única com largura máxima de 480px. Fundo claro com logótipo da clínica ao centro no topo. Design minimalista com ícone de envelope/cadeado no topo.

**Elementos de UI presentes**

- Título "Recuperar Palavra-passe"
- Descrição: "Introduza o seu e-mail e enviaremos um link para redefinir a sua palavra-passe"
- Campo `email` (email, obrigatório)
- Mensagem de sucesso (`mensagemEnviada`) exibida após pedido bem-sucedido
- Botão "Enviar Link" (submit)
- Hiperligação "Voltar ao Login" para `/login`

**Comportamentos interativos**

- Submissão via POST para `/recuperar-senha`
- O controller invoca `recuperacaoSenhaService.iniciarRecuperacao(email)` que envia email com token
- Por segurança, a mensagem de sucesso é sempre exibida independentemente de o email existir na base de dados (evita enumeração de contas)
- Após sucesso, o formulário é ocultado e apenas a mensagem de confirmação é exibida

**Fluxos de navegação**

- `POST /recuperar-senha` (sucesso) → permanece com mensagem de sucesso
- Hiperligação → `/login`
- Email recebido contém link para `/redefinir-senha?token=...`

---

### 10.2.1.4 Página de Redefinição de Palavra-Passe (`/redefinir-senha`)

**Descrição geral e objetivo no fluxo**

Página acedida através do link enviado por email. Permite ao utilizador definir uma nova palavra-passe após validação do token de recuperação.

**Layout e estrutura visual**

Layout centrado de coluna única, semelhante à página de recuperação. Fundo claro com ícone de cadeado no topo.

**Elementos de UI presentes**

- Título "Redefinir Palavra-passe"
- Mensagem de erro de token inválido/expirado (`erroToken`), com hiperligação para solicitar novo link
- Campo `token` (hidden) — transporta o token de recuperação
- Campo `novaSenha` (password, obrigatório) — "Nova Palavra-passe *"
- Campo `confirmarSenha` (password, obrigatório) — "Confirmar Palavra-passe *"
- Botão de alternância de visibilidade (olho) para cada campo de password (JavaScript inline)
- Texto de ajuda com requisitos da palavra-passe (exibido condicionalmente quando o campo não tem erro)
- Mensagens de erro de validação por campo
- Botão "Redefinir Palavra-passe" (submit)
- Hiperligação "Voltar ao Login" para `/login`

**Comportamentos interativos**

- Validação server-side: `@NotBlank`, `@ValidPassword` para `novaSenha` e `confirmarSenha`
- Verificação de igualdade entre `novaSenha` e `confirmarSenha` no controller (validação cross-campo)
- Validação do token: verifica se o token existe e não expirou
- Submissão via POST para `/redefinir-senha`
- JavaScript para alternar visibilidade das passwords (toggle entre `type="password"` e `type="text"`)
- Em caso de token inválido, exibe erro e oculta o formulário
- Em caso de sucesso, redireciona para `/login?redefinida=true`

**Fluxos de navegação**

- `GET /redefinir-senha?token=...` (token válido) → mostra formulário
- `GET /redefinir-senha?token=...` (token inválido) → mostra erro
- `POST /redefinir-senha` (sucesso) → redireciona para `/login?redefinida=true`
- `POST /redefinir-senha` (erro) → permanece com mensagens de erro
- Hiperligação → `/login`

---

### 10.2.1.5 Página Principal — Consultas/Agenda (`/consultas`)

**Descrição geral e objetivo no fluxo**

A página de Consultas é o ecrã principal e central da aplicação. Funciona como dashboard de agenda para todos os tipos de utilizador. A rececionista vê todas as consultas; o paciente vê apenas as suas. A página permite filtrar consultas por estado, período, dentista, tipo e texto de pesquisa, e apresenta cards visuais com informação resumida e ações contextuais.

**Layout e estrutura visual**

Layout de três secções verticais:
- **Cabeçalho sticky** com navegação principal (Consultas, Marcar Consulta, Faturas, Perfil), nome do utilizador logado e botão de logout
- **Secção de herói** com saudação personalizada "Bem-vinda de volta, [nome]" para rececionista ou "Olá, [nome]" para paciente, indicador de total de consultas e próxima visita
- **Corpo principal** dividido em duas colunas:
  - **Coluna lateral esquerda (280px):** cartão de resumo rápido (total de consultas, próxima visita, período de consultas), e cartão de dica de saúde bucal (imagem ilustrativa + texto)
  - **Coluna principal (expansível):** barra de filtros (selects e campos de data) seguida de lista de cards de consulta

**Elementos de UI presentes**

- Navegação sticky: Consultas (ativo), Marcar Consulta, Faturas, Perfil (ícone de utilizador)
- Botão de logout (texto "Sair")
- Secção de saudação personalizada com nome do utilizador
- Indicadores: total de consultas, data da próxima visita, período abrangido
- Barra de filtros com:
  - Select `estado` — AGENDADA, CONFIRMADA, CONCLUIDA, CANCELADA, FATURADA
  - Select `periodo` — HOJE, FUTURAS, PASSADAS
  - Select `dentistaId` — dinâmico (apenas para staff)
  - Select `tipoConsulta` — dinâmico
  - Campo `pesquisa` (texto) — visível para staff (filtrar por paciente), oculto para pacientes
  - Campo `dataInicio` e `dataFim` (date) — intervalo de datas
  - Botão "Filtrar" e hiperligação "Limpar Filtros"
- Lista de cards de consulta, cada um com:
  - Borda lateral esquerda colorida conforme o estado (classe `borderClass`)
  - Nome do paciente, dentista, procedimento, data, hora
  - Badge de estado com cor específica (`badgeClass`)
  - Botões de ação: Reagendar, Cancelar (apenas se `podeAlterar` for true)
- Mensagem de vazio condicional: "Nenhuma consulta encontrada" vs "Nenhuma consulta registada"
- Indicador de filtros aplicados na mensagem de vazio
- Mensagens flash: sucesso (`mensagemSucesso`) ou erro (`mensagemErro`)
- Banner de confirmação de marcação (`param.marcada`)

**Comportamentos interativos**

- Submissão do formulário de filtros via GET para `/consultas` (preserva estado para bookmarking)
- Todos os filtros são opcionais e combinados com AND
- Para utilizadores do tipo PACIENTE: `pesquisa` e `dentistaId` são ignorados; filtra apenas as consultas do paciente
- Cards com `podeAlterar = false` (consultas passadas ou em estados não alteráveis) não exibem botões de ação
- Preenchimento automático de datas `dataInicio`/`dataFim` quando o período é selecionado
- Navegação para Marcar Consulta, Consulta (reagendar/cancelar), Perfil, Faturas

**Fluxos de navegação**

- Navegação "Marcar Consulta" → `/marcar-consulta`
- Botão "Reagendar" → `/consultas/{id}/reagendar`
- Botão "Cancelar" → `/consultas/{id}/cancelar`
- Navegação "Faturas" → `/faturas` (staff) ou `/paciente/faturas` (paciente)
- Navegação "Perfil" → `/perfil`
- Logout → `POST /logout`

**Considerações de responsividade**

Em ecrãs ≥1024px, layout de duas colunas com sidebar fixa. Em ecrãs médios (768–1023px), a sidebar colapsa para uma secção no topo. Em telemóveis (<768px), a barra de filtros é empilhada verticalmente, os selects ocupam 100% de largura e os cards de consulta simplificam a informação para dados essenciais.

---

### 10.2.1.6 Página de Marcação de Consulta (`/marcar-consulta`)

**Descrição geral e objetivo no fluxo**

Página que permite ao paciente (ou rececionista) agendar uma nova consulta. Apresenta um assistente de 3 passos para selecionar dentista, data e hora, com atualização dinâmica dos horários disponíveis via JavaScript.

**Layout e estrutura visual**

Layout de duas colunas:
- **Coluna principal (expansível):** três secções de seleção sequenciais:
  1. **Escolher Especialista:** Cards de rádio com imagem ilustrativa, nome e especialidade de cada dentista
  2. **Escolher Data:** Grelha de rádio com os próximos 10 dias úteis, apresentando dia da semana, número do dia e mês
  3. **Escolher Hora:** Grelha de rádio com horários disponíveis, carregados dinamicamente
- **Coluna lateral (300px):** cartão de resumo "Detalhes da Marcação" com select para tipo de consulta e confirmação visual dos itens selecionados

**Elementos de UI presentes**

- Navegação sticky (Consultas, Marcar Consulta ativo, Faturas, Perfil)
- Título "Marcar Consulta" com subtítulo
- Secção de especialistas: cards rádio com imagem, nome e especialidade
- Secção de datas: grelha de botões rádio com dia da semana, número e mês
- Secção de horários: grelha de botões rádio com horários "HH:mm"
- Mensagem "Sem horários disponíveis" quando a lista de horários está vazia
- Select `tipo` para tipo de consulta: Consulta Geral, Ortodontia, Estética, Implantologia
- Mensagem de erro (`erroMarcacao`)
- Botão "Confirmar Marcação" (submit)
- Fragmentos legais (modais de Termos e Privacidade)

**Comportamentos interativos**

- Seleção de dentista e data aciona pedido AJAX via `fetch('/consultas/horarios-disponiveis?dentistaId=X&data=YYYY-MM-DD')` que retorna JSON com lista de horários disponíveis
- JavaScript atualiza a grelha de horários sem recarregar a página
- Apenas PACIENTE pode submeter; staff vê erro "Apenas pacientes podem marcar consultas através desta página"
- Se o utilizador não estiver autenticado, redireciona para `/login`
- Após confirmação bem-sucedida, redireciona para `/consultas?marcada=sucesso`
- Proteção CSRF no formulário

**Fluxos de navegação**

- `POST /marcar-consulta` (sucesso) → redirecionamento para `/consultas?marcada=sucesso`
- `POST /marcar-consulta` (erro) → permanece com `erroMarcacao`
- Navegação "Consultas" → `/consultas`
- Navegação "Faturas" → `/faturas` ou `/paciente/faturas`

**Considerações de responsividade**

Os cards de especialista adaptam-se a 2 colunas em tablets e 1 coluna em telemóveis. A grelha de datas e horários reorganiza-se para 2-3 colunas em ecrãs pequenos. A coluna lateral de resumo posiciona-se abaixo do conteúdo principal em telemóveis.

---

### 10.2.1.7 Página de Reagendamento de Consulta (`/consultas/{id}/reagendar`)

**Descrição geral e objetivo no fluxo**

Página que permite ao paciente ou rececionista alterar a data e hora de uma consulta existente. Apresenta o resumo da consulta atual e permite selecionar nova data (próximos 10 dias) e horário disponível.

**Layout e estrutura visual**

Layout de duas colunas:
- **Coluna principal:** breadcrumb "Consultas > Reagendar Consulta", cartão com detalhes da consulta atual (tipo, dentista, data/hora, estado), seguido do formulário de reagendamento com seleção de data (passo 1) e hora (passo 2)
- **Coluna lateral (300px):** cartão de resumo da consulta atual para confirmação visual

**Elementos de UI presentes**

- Navegação sticky padronizada
- Breadcrumb "Consultas > Reagendar Consulta"
- Cartão de consulta atual com: tipo, dentista (nome completo), data/hora formatada, estado (descrição textual)
- Selector de data: grelha de botões rádio com próximos 10 dias
- Selector de hora: grelha de botões rádio carregada dinamicamente
- Mensagem de erro de reagendamento (`erroReagendamento`)
- Botão "Confirmar Reagendamento" (submit)

**Comportamentos interativos**

- Carregamento inicial dos horários disponíveis para a primeira data selecionada
- Alteração da data aciona pedido AJAX via `fetch('/consultas/{id}/horarios-disponiveis-reagendamento?data=YYYY-MM-DD')`
- O cálculo de disponibilidade exclui o slot da própria consulta que está a ser reagendada (a consulta não conflita consigo mesma)
- Área JavaScript com `/*<![CDATA[*/` para passar dados do modelo Thymeleaf para JS
- Valida server-side se a data/hora está disponível antes de confirmar
- Verifica que o utilizador tem permissão para reagendar (propriedade da consulta para pacientes)
- Após sucesso, redireciona para `/consultas`

**Fluxos de navegação**

- Breadcrumb "Consultas" → `/consultas`
- `POST /consultas/{id}/reagendar` (sucesso) → `/consultas`
- `POST /consultas/{id}/reagendar` (erro) → permanece com `erroReagendamento`

---

### 10.2.1.8 Página de Cancelamento de Consulta (`/consultas/{id}/cancelar`)

**Descrição geral e objetivo no fluxo**

Página para cancelar uma consulta existente. Apresenta um aviso de irreversibilidade, os detalhes da consulta e um formulário para selecionar motivo de cancelamento com opção de texto livre.

**Layout e estrutura visual**

Layout centrado de coluna única com largura máxima de 720px:
- **Cabeçalho:** breadcrumb "Consultas > Cancelar Consulta"
- **Banner de aviso:** fundo vermelho claro com ícone de alerta, texto "Tem a certeza que deseja cancelar esta consulta?" e sugestão para considerar reagendar
- **Cartão da consulta:** tipo, especialista, data formatada, hora formatada
- **Formulário de cancelamento:** seleção de motivo e área de texto

**Elementos de UI presentes**

- Breadcrumb "Consultas > Cancelar Consulta"
- Banner de aviso com ícone de alerta triângulo
- Hiperligação para reagendar (dentro do aviso)
- Cartão informativo da consulta com: tipo (`consulta.tipo`), dentista (nome completo condicional), data (formatada dd/MM/yyyy), hora (formatada HH:mm)
- Botões de rádio de motivo predefinido:
  - "Indisponibilidade pessoal"
  - "Conflito de agenda"
  - "Motivo de saúde"
  - "Outro motivo"
- Área de texto `motivo` (opcional, máximo 500 caracteres) com contador de caracteres em tempo real (JavaScript)
- Campo oculto `motivo-final` combinado por JavaScript
- Botão "Confirmar Cancelamento" (submit), desativado se nenhum motivo selecionado
- Fragmentos legais

**Comportamentos interativos**

- JavaScript combina o motivo selecionado (rádio) com o texto livre (textarea) e insere o resultado no campo oculto `motivo-final`
- Se o rádio "Outro motivo" estiver selecionado, o textarea é obrigatório e o botão só é ativado quando preenchido
- Contador de caracteres (max 500) atualizado em tempo real no textarea
- Validação server-side: verifica permissão (paciente só pode cancelar as suas consultas)
- Após sucesso, redireciona para `/consultas` com mensagem de sucesso via flash attribute
- Hiperligação "Reagendar" no aviso aponta para `/consultas/{id}/reagendar`

**Fluxos de navegação**

- Breadcrumb "Consultas" → `/consultas`
- `POST /consultas/{id}/cancelar` (sucesso) → `/consultas` com `mensagemSucesso`
- `POST /consultas/{id}/cancelar` (erro) → `/consultas` com `mensagemErro`
- Hiperligação "reagendar" → `/consultas/{id}/reagendar`

---

### 10.2.1.9 Página de Perfil do Utilizador (`/perfil`)

**Descrição geral e objetivo no fluxo**

Página de edição de perfil pessoal. Permite ao utilizador autenticado visualizar e editar os seus dados pessoais, contactos e morada. Inclui preenchimento automático de localidade a partir do código postal.

**Layout e estrutura visual**

Layout de duas colunas:
- **Coluna lateral esquerda (320px):** fotografia de perfil (placeholder com iniciais), nome completo, tipo de utilizador, botão de logout
- **Coluna principal:** formulário de edição organizado em 3 secções:
  - "Informações Pessoais": primeiroNome, ultimoNome, dataNascimento, NIF
  - "Contacto": email, telemóvel, telefone
  - "Morada": rua, numeroPorta, códigoPostal, localidade (readonly)

**Elementos de UI presentes**

- Avatar circular com iniciais do utilizador
- Nome completo e tipo de utilizador (ex: "Rececionista", "Paciente")
- Botão de logout (POST `/logout`)
- Mensagem de sucesso (`param.atualizado`)
- Formulário com 11 campos organizados em 3 secções com títulos
- Indicadores de erro (`has-error`) em campos inválidos
- Mensagens de erro individuais por campo (`th:errors`)
- Campo `localidade` readonly, preenchido automaticamente
- Botão "Guardar Alterações" (submit)

**Comportamentos interativos**

- Validação server-side via `@Valid PerfilForm`: `@NotBlank`, `@Size(min=2)`, `@Pattern` em primeiroNome e ultimoNome; `@Past` em dataNascimento; `@ValidNif`; `@Email`; `@ValidTelefonePortugues`; `@Pattern` em codigoPostal
- JavaScript para pesquisa automática de localidade via `fetch('/codigos-postais/{codigoPostal}')` com debounce
- Formatação automática do código postal (inserção de hífen após 4 dígitos)
- `codigoPostal` limitado a 8 caracteres (XXXX-XXX)
- Após sucesso, `utilizadorNome` na sessão é atualizado
- Redirecionamento para `/perfil?atualizado=true`

**Fluxos de navegação**

- `POST /perfil` (sucesso) → `/perfil?atualizado=true`
- `POST /perfil` (erro) → permanece com erros de validação
- Navegação principal para Consultas, Marcar Consulta, Faturas
- Logout → `POST /logout`

---

### 10.2.1.10 Página de Faturas — Visão Geral (`/faturas`)

**Descrição geral e objetivo no fluxo**

Página de listagem de faturas e recibos para utilizadores do tipo staff (rececionista, administrador, dentista). Apresenta uma tabela com todas as faturas emitidas, permitindo visualizar detalhes e fazer download do PDF.

**Layout e estrutura visual**

Layout de página única:
- Cabeçalho sticky com navegação (Consultas, Marcar Consulta, Faturas ativo, Perfil)
- Título "Faturas e Recibos"
- Barra de filtros estática (apenas visual, sem action): filtro por ano e campo de pesquisa
- Tabela de faturas com: ícone de tratamento, nome do tratamento, dentista, data de emissão, valor (formatado EUR), badge de estado, ações (download PDF)
- Cartão de suporte à direita: "Dúvidas sobre faturação?"
- Cartão "Dados Protegidos" com informações de segurança

**Elementos de UI presentes**

- Navegação sticky padronizada
- Título e descrição da secção
- Tabela de faturas com colunas: Tratamento, Data de Emissão, Valor, Estado, Ações
- Cada linha: ícone de documento/ticket, nome do tratamento, dentista, data, valor, badge de estado
- Botão "Descarregar PDF" (condicional: apenas se `pdfDisponivel` for true)
- Texto "PDF indisponível" quando não há PDF
- Mensagem de lista vazia com ícone
- Cartão informativo de suporte
- Cartão de segurança de dados
- Fragmentos legais

**Comportamentos interativos**

- Se a lista `faturas` estiver vazia, exibe mensagem "Nenhuma fatura encontrada"
- Iteração sobre `faturas` com `th:each`
- O link de download aponta para `/faturas/{id}/download` e retorna o PDF como attachment
- Pacientes não têm acesso a esta rota (são redirecionados se tentarem aceder, mas o controller não diferencia — a lista mostra todas)

**Fluxos de navegação**

- Navegação "Consultas" → `/consultas`
- Navegação "Marcar Consulta" → `/marcar-consulta`
- Navegação "Perfil" → `/perfil`
- Botão "Descarregar PDF" → `/faturas/{id}/download`

---

### 10.2.1.11 Página de Faturas — Visão Paciente (`/paciente/faturas`)

**Descrição geral e objetivo no fluxo**

Página de listagem de faturas específica para o paciente autenticado. Exibe apenas as faturas do paciente logado, com layout responsivo em grelha (desktop) ou blocos (mobile).

**Layout e estrutura visual**

- Cabeçalho sticky com navegação (Consultas, Marcar Consulta, Faturas ativo, Perfil)
- Título "Faturas e Recibos"
- Grelha de faturas em formato de lista (não tabela pura) com layout adaptável:
  - Desktop: grelha com colunas Tratamento, Data, Valor, Estado, Documento
  - Mobile: blocos com label + valor
- Cada item: ícone, descrição + número da fatura, data, valor formatado, badge de estado, botão de download
- Mensagem de vazio com ícone
- Cartões informativos no final: "Dúvidas sobre faturação?" e "Dados Protegidos"

**Elementos de UI presentes**

- Navegação sticky padronizada
- Título da secção
- Grelha de faturas com itens contendo: descrição (`fatura.descricao()`), número (`fatura.id()`), data (`fatura.dataEmissao()`), valor (`fatura.valor()`), badge de estado com cor (`fatura.corBadge()`), texto de estado (`fatura.estadoLabel()`)
- Botão de download condicional:
  - Se `podeBaixar()`: hiperligação para `/paciente/faturas/{id}/download`
  - Se `estadoCodigo == 'PAGA' e !podeBaixar()`: "PDF a ser processado..."
  - Se `estadoCodigo != 'PAGA'`: "A aguardar pagamento"
- Indicador de vazio
- Cartões de suporte e segurança

**Comportamentos interativos**

- A lista é filtrada automaticamente pelo `utilizadorId` na sessão — o paciente vê apenas as suas faturas
- Download do PDF verifica propriedade (lança `FaturaAcessoNegadoException` se outro paciente tentar aceder)
- Layout responsivo com CSS grid que adapta o número de colunas

**Fluxos de navegação**

- Navegação "Consultas" → `/consultas`
- Navegação "Marcar Consulta" → `/marcar-consulta`
- Navegação "Perfil" → `/perfil`
- Download PDF → `/paciente/faturas/{id}/download`

---

## 10.2.2 Controllers (Spring MVC)

### 10.2.2.1 `LoginController`

**Ficheiro:** `controller/LoginController.java`

**Responsabilidade principal:** Gerir a autenticação de utilizadores e o fluxo de login/logout.

**URL base:** Não tem `@RequestMapping` de classe; mapeamentos individuais.

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `login()` | `@GetMapping` | `{"/", "/login"}` | `HttpSession session` | Se sessão tem `utilizadorId`, redireciona para `/consultas`. Caso contrário, mostra formulário de login. | — | `login/index` |
| `autenticar()` | `@PostMapping` | `/login` | `@RequestParam String email`, `@RequestParam String password`, `HttpSession session`, `Model model` | Invoca `utilizadorService.autenticar(email, password)`. Se falhar, adiciona `erroLogin` e `emailInformado` ao modelo e retorna à mesma vista. Se sucesso, define `utilizadorId`, `utilizadorNome`, `utilizadorTipo`, `utilizadorNif` na sessão e redireciona para `/consultas`. | `erroLogin` (String), `emailInformado` (String) | `login/index` (erro) ou redirect `/consultas` |
| `dashboard()` | `@GetMapping` | `/dashboard` | — | Redireciona para `/consultas` (rota de compatibilidade). | — | redirect `/consultas` |
| `logout()` | `@PostMapping` | `/logout` | `HttpSession session` | Invoca `session.invalidate()` e redireciona para `/login`. | — | redirect `/login` |

**Tratamento de erros:** A autenticação falhada não lança exceção — o serviço retorna `null` e o controller adiciona uma mensagem de erro genérica ao modelo.

**Aspetos técnicos relevantes:**
- A sessão HTTP (`HttpSession`) é o mecanismo de estado de autenticação
- Não utiliza Spring Security para autenticação — a verificação é manual em cada controller
- O redirecionamento para `/consultas` em caso de sessão ativa evita dupla autenticação
- `utilizadorService.autenticar()` utiliza `BCryptPasswordEncoder` para verificar a palavra-passe

---

### 10.2.2.2 `CadastroController`

**Ficheiro:** `controller/CadastroController.java`

**Responsabilidade principal:** Gerir o auto-registo de novos pacientes na plataforma.

**URL base:** `/cadastro`

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `mostrarFormulario()` | `@GetMapping` | `/cadastro` | `HttpSession session`, `Model model` | Se sessão ativa, redireciona para `/consultas`. Caso contrário, apresenta formulário com `CadastroForm` vazio. | `cadastroForm` (new CadastroForm) | `cadastro/index` |
| `processar()` | `@PostMapping` | `/cadastro` | `@Valid @ModelAttribute("cadastroForm") CadastroForm form`, `BindingResult result`, `Model model` | Valida o formulário. Se erros, retorna à vista. Se válido, cria `Utilizador` (tipo PACIENTE, status ATIVO, password encriptada com BCrypt) e `Paciente` associado através de `pacienteService.salvar()`. Redireciona para `/login?cadastro=sucesso`. | `cadastroForm` (re-populado em caso de erro) | `cadastro/index` (erro) ou redirect `/login?cadastro=sucesso` |

**DTO vinculado:** `CadastroForm` com campos `nome`, `email`, `telefone`, `password`, `confirmPassword`, `termos`. Validações: `@NotBlank`, `@Email`, `@Pattern` (nome ≥2 palavras), `@ValidPassword`, `@ValidTelefonePortugues`, `@AssertTrue` (termos), `@PasswordMatches` (custom).

**Tratamento de erros:** Erros de validação são apresentados individualmente por campo via `th:errors`; erros globais (`@PasswordMatches`) são exibidos na secção de erros globais.

**Aspetos técnicos relevantes:**
- O nome completo é dividido em `primeiroNome` e `ultimoNome` pelo espaço
- Se o telefone não for preenchido, é guardado como `null`
- O email é convertido para minúsculas antes de ser persistido
- A password é encriptada com `BCryptPasswordEncoder` antes de ser guardada

---

### 10.2.2.3 `ConsultasController`

**Ficheiro:** `controller/ConsultasController.java`

**Responsabilidade principal:** Apresentar a lista de consultas com filtros, e gerir os fluxos de reagendamento e cancelamento.

**URL base:** `/consultas`

**Registos internos:**
- `ConsultaView(Integer id, String paciente, String dentista, String procedimento, String data, String hora, String status, String borderClass, String badgeClass, boolean podeAlterar)` — DTO para apresentação
- `DataOpcao(String valor, String diaSemana, String diaMes, String mes)` — opção de data no reagendamento
- `EstadoOpcao(EstadoConsulta valor, String label)` — opção de filtro de estado
- `DentistaOpcao(Integer id, String nome)` — opção de filtro de dentista

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `consultas()` | `@GetMapping` | `/consultas` | `HttpSession session`, `Model model`, `@RequestParam(required=false) String estado`, `@RequestParam(required=false) String periodo`, `@RequestParam(required=false) @DateTimeFormat LocalDate dataInicio`, `@RequestParam(required=false) @DateTimeFormat LocalDate dataFim`, `@RequestParam(required=false) Integer dentistaId`, `@RequestParam(required=false) String pesquisa`, `@RequestParam(required=false) String tipoConsulta` | Se não autenticado, redireciona para `/login`. Carrega consultas e aplica filtros. Se utilizador é PACIENTE, força `pacienteId` e esconde `pesquisa`/`dentistaId`. Popula selects de filtro e lista de `ConsultaView` no modelo. | `nomeUtilizador`, `totalConsultas`, `proximaVisita`, `periodoConsultas`, `consultas` (List\<ConsultaView\>), `estadosConsulta`, `dentistas`, `tiposConsulta`, `estadoSelecionado`, `periodoSelecionado`, `dataInicio`, `dataFim`, `dentistaSelecionadoId`, `mostrarFiltroPaciente`, `pesquisa`, `tipoConsultaSelecionado`, `filtrosAplicados` | `consultas/index` |
| `mostrarCancelamento()` | `@GetMapping` | `/consultas/{id}/cancelar` | `@PathVariable Integer id`, `HttpSession session`, `Model model` | Redireciona se não autenticado. Carrega a `Consulta` por ID e passa ao modelo. | `consulta` (entity Consulta) | `cancelar-consulta/index` |
| `confirmarCancelamento()` | `@PostMapping` | `/consultas/{id}/cancelar` | `@PathVariable Integer id`, `@RequestParam(defaultValue="") String motivo`, `HttpSession session`, `RedirectAttributes redirectAttributes` | Verifica permissão (PACIENTE só pode cancelar as suas). Invoca `consultaService.cancelar()`. Redireciona com flash attribute de sucesso/erro. | — | redirect `/consultas` |
| `mostrarReagendar()` | `@GetMapping` | `/consultas/{id}/reagendar` | `@PathVariable Integer id`, `HttpSession session`, `Model model` | Carrega consulta e calcula datas/horários disponíveis (próximos 10 dias). | `consulta`, `datas` (List\<DataOpcao\>), `horarios` (List\<String\>), `dataSelecionada` | `reagendar-consulta/index` |
| `confirmarReagendamento()` | `@PostMapping` | `/consultas/{id}/reagendar` | `@PathVariable Integer id`, `@RequestParam String data`, `@RequestParam String hora`, `HttpSession session`, `Model model`, `RedirectAttributes redirectAttributes` | Valida data/hora, invoca `consultaService.reagendar()`. Redireciona com sucesso ou retorna erro. | `consulta`, `datas`, `horarios`, `dataSelecionada`, `erroReagendamento` | `reagendar-consulta/index` (erro) ou redirect `/consultas` |
| `horariosDisponiveisReagendamento()` | `@GetMapping` + `@ResponseBody` | `/consultas/{id}/horarios-disponiveis-reagendamento` | `@PathVariable Integer id`, `@RequestParam @DateTimeFormat LocalDate data` | Calcula e retorna JSON com horários disponíveis, excluindo o slot da própria consulta. | — | JSON `List<String>` |

**Tratamento de erros:** Exceções do serviço (ex: `consultaService.cancelar()`) são capturadas e convertidas em mensagens flash.

**Aspetos técnicos relevantes:**
- Horários disponíveis fixos: 09:00–11:30 (intervalos de 30 min) e 14:00–15:30
- O reagendamento exclui o próprio horário da consulta para evitar conflito consigo mesma
- Flash attributes (`RedirectAttributes`) para mensagens após redirect

---

### 10.2.2.4 `MarcarConsultaController`

**Ficheiro:** `controller/MarcarConsultaController.java`

**Responsabilidade principal:** Gerir o fluxo de marcação de novas consultas por pacientes.

**URL base:** `/marcar-consulta` e `/consultas/horarios-disponiveis`

**Registos internos:**
- `DentistaOpcao(Integer id, String nome, String especialidade)`
- `DataOpcao(String valor, String diaSemana, String diaMes, String mes)`

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `marcarConsulta()` | `@GetMapping` | `/marcar-consulta` | `HttpSession session`, `Model model` | Redireciona se não autenticado. Carrega dentistas, datas (próximos 10 dias) e horários disponíveis para a primeira combinação. Se utilizador não é PACIENTE, adiciona `erroMarcacao`. | `dentistas`, `datas`, `horarios`, `dentistaSelecionado`, `dataSelecionada`, `horaSelecionada`, `tipoSelecionado`, `erroMarcacao` | `marcar-consulta/index` |
| `confirmarMarcacao()` | `@PostMapping` | `/marcar-consulta` | `@RequestParam Integer dentistaId`, `@RequestParam String data`, `@RequestParam String hora`, `@RequestParam(defaultValue="Consulta Geral") String tipo`, `HttpSession session`, `Model model` | Apenas PACIENTE. Cria `Consulta` com status AGENDADA, duração 45 min, `dataHoraInicio` (data+hora combinadas), `dataMarcacao` (now). Invoca `consultaService.agendarConsulta()`. | `erroMarcacao` | `marcar-consulta/index` (erro) ou redirect `/consultas?marcada=sucesso` |
| `horariosDisponiveis()` | `@GetMapping` + `@ResponseBody` | `/consultas/horarios-disponiveis` | `@RequestParam Integer dentistaId`, `@RequestParam @DateTimeFormat LocalDate data` | Calcula e retorna JSON com lista de horários "HH:mm" disponíveis para o dentista na data. | — | JSON `List<String>` |

**Tratamento de erros:** Se o utilizador não é PACIENTE, a marcação é bloqueada com mensagem no modelo. Erros do serviço (ex: conflito de horário) são capturados e exibidos.

**Aspetos técnicos relevantes:**
- Endpoint REST `horariosDisponiveis()` é consumido via `fetch()` pelo JavaScript em `marcar-consulta/index.html`
- 12 horários possíveis: 09:00–11:30 e 14:00–16:30 em intervalos de 30 min
- Duração fixa de 45 minutos por consulta
- O tipo de consulta padrão é "Consulta Geral"

---

### 10.2.2.5 `PerfilController`

**Ficheiro:** `controller/PerfilController.java`

**Responsabilidade principal:** Gerir a visualização e edição do perfil do utilizador autenticado.

**URL base:** `/perfil`

**DTO vinculado:** `PerfilForm` com 11 campos validados.

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `mostrarPerfil()` | `@GetMapping` | `/perfil` | `HttpSession session`, `Model model` | Redireciona se não autenticado. Carrega `Utilizador` do serviço e popula `PerfilForm` com os dados atuais. | `perfilForm` (PerfilForm), `nomeCompleto` (String), `tipoUtilizador` (String) | `perfil/index` |
| `atualizarPerfil()` | `@PostMapping` | `/perfil` | `@Valid @ModelAttribute("perfilForm") PerfilForm form`, `BindingResult result`, `HttpSession session`, `Model model` | Valida formulário. Se válido, atualiza campos do `Utilizador` e persiste via `utilizadorService.salvar()`. Atualiza `utilizadorNome` na sessão. | `perfilForm`, `nomeCompleto`, `tipoUtilizador` | `perfil/index` (erro) ou redirect `/perfil?atualizado=true` |

**Tratamento de erros:** Erros de validação individuais por campo; erros globais (ex: se houver) na secção de erros globais.

**Aspetos técnicos relevantes:**
- A localidade (`localidade`) é apenas de visualização (readonly no template) — é preenchida automaticamente pelo JavaScript cliente
- `codigoPostal` segue formato português "XXXX-XXX" validado por regex
- `nif` validado por `@ValidNif` (validador customizado que implementa a lógica de validação dos dígitos de controlo do NIF português)
- `ValidTelefonePortugues` valida números de telemóvel portugueses (9 dígitos começados por 9)

---

### 10.2.2.6 `FaturasController`

**Ficheiro:** `controller/FaturasController.java`

**Responsabilidade principal:** Listar faturas e servir ficheiros PDF para download (visão geral — staff).

**URL base:** `/faturas`

**Registo interno:** `FaturaView(Integer id, String tratamento, String dentista, String dataEmissao, String valor, String estado, String classeEstado, String pontoEstado, boolean pdfDisponivel)`

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `faturas()` | `@GetMapping` | `/faturas` | `HttpSession session`, `Model model` | Redireciona se não autenticado. Carrega lista de faturas e converte para `FaturaView`. Pacientes vêm todas as faturas (sem distinção de role no controller). | `faturas` (List\<FaturaView\>) | `faturas/index` |
| `descarregarFatura()` | `@GetMapping` | `/faturas/{id}/download` | `@PathVariable Integer id`, `HttpSession session` | Verifica autenticação, carrega fatura, verifica estado PAGA, resolve caminho do PDF no sistema de ficheiros, retorna como `Resource` attachment. | — | PDF file download |

**Tratamento de erros:** Se não autenticado, redireciona. Se fatura não encontrada ou PDF não existe, retorna 404 ou erro.

**Aspetos técnicos relevantes:**
- O download do PDF usa `ResourceLoader` para resolver `file:` URLs
- Cabeçalho `Content-Disposition: attachment` com nome do ficheiro
- O diretório de faturas é configurado em `app.faturas-dir` no `application.properties`
- A classe de estado (`classeEstado`) e ponto (`pontoEstado`) são usados para estilização CSS de badges

---

### 10.2.2.7 `FaturaDownloadController`

**Ficheiro:** `controller/FaturaDownloadController.java`

**Responsabilidade principal:** Listar faturas e servir PDFs para pacientes autenticados (visão restrita ao paciente logado).

**URL base:** `/paciente/faturas`

**Registo interno:** `FaturaView(Integer id, String descricao, String dataEmissao, String valor, String estadoCodigo, String estadoLabel, String corBadge, boolean podeBaixar, boolean temPdf)`

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `listarFaturas()` | `@GetMapping` | `/paciente/faturas` | `HttpSession session`, `Model model` | Redireciona se não autenticado. Invoca `faturaService.listarPorPaciente(utilizadorId)` para obter apenas as faturas do paciente logado. | `faturas` (List\<FaturaView\>), `nomeUtilizador` | `paciente/faturas` |
| `descarregarFatura()` | `@GetMapping` | `/paciente/faturas/{id}/download` | `@PathVariable Integer id`, `HttpSession session` | Verifica autenticação. Invoca `faturaService.buscarPorIdEPaciente(id, utilizadorId)` que valida propriedade (lança `FaturaAcessoNegadoException` se não pertencer ao paciente). Verifica estado PAGA. Serve PDF. | — | PDF file download |

**Tratamento de erros:**
- `FaturaAcessoNegadoException` é lançada se um paciente tentar aceder a uma fatura que não lhe pertence
- PDF indisponível se estado diferente de PAGA
- Logs de debug para diagnóstico de caminhos de ficheiros

**Aspetos técnicos relevantes:**
- Separação clara entre "staff vê todas" (FaturasController) e "paciente vê só as suas" (FaturaDownloadController)
- `buscarPorIdEPaciente()` é um método de segurança que previne acesso cruzado a dados
- `corBadge` no DTO permite estilizar badges de estado sem lógica condicional no template

---

### 10.2.2.8 `RecuperacaoSenhaController`

**Ficheiro:** `controller/RecuperacaoSenhaController.java`

**Responsabilidade principal:** Gerir o fluxo de recuperação e redefinição de palavra-passe.

**URL base:** `/recuperar-senha` e `/redefinir-senha`

**DTO vinculado:** `RedefinirSenhaForm` com campos `token`, `novaSenha`, `confirmarSenha`.

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Modelo | Vista |
|--------|----------|-----|------------|--------|--------|-------|
| `mostrarFormularioRecuperacao()` | `@GetMapping` | `/recuperar-senha` | — | Apresenta formulário de pedido de recuperação. | — | `recuperar-senha/index` |
| `processarPedidoRecuperacao()` | `@PostMapping` | `/recuperar-senha` | `@RequestParam String email`, `RedirectAttributes redirectAttributes` | Invoca `recuperacaoSenhaService.iniciarRecuperacao(email)`. Exibe sempre mensagem de sucesso (segurança — evita enumeração). | Flash: `mensagemEnviada` | redirect `/recuperar-senha?enviado=true` |
| `mostrarFormularioRedefinicao()` | `@GetMapping` | `/redefinir-senha` | `@RequestParam(required=false) String token`, `Model model` | Valida token. Se inválido/expirado, adiciona `erroToken` ao modelo. Se válido, apresenta formulário com token oculto. | `erroToken`, `redefinirSenhaForm` (RedefinirSenhaForm) | `redefinir-senha/index` |
| `processarRedefinicao()` | `@PostMapping` | `/redefinir-senha` | `@Valid @ModelAttribute("redefinirSenhaForm") RedefinirSenhaForm form`, `BindingResult result`, `Model model`, `RedirectAttributes redirectAttributes` | Valida formulário. Verifica se passwords coincidem (validação cross-campo no controller). Verifica token ainda válido. Invoca `recuperacaoSenhaService.redefinirSenha()`. | `erroToken`, `redefinirSenhaForm` | `redefinir-senha/index` (erro) ou redirect `/login?redefinida=true` |

**Tratamento de erros:**
- Token inválido/expirado → `erroToken` com link para novo pedido
- Passwords não coincidem → erro global no formulário
- Validações @Valid para formato da password

**Aspetos técnicos relevantes:**
- O serviço `iniciarRecuperacao()` envia email via JavaMailSender (configurado para Gmail SMTP)
- O token tem validade temporal (verificada pelo serviço)
- Mensagem de sucesso invariável evita vazamento de informação
- `@ValidPassword` garante complexidade mínima da nova password

---

### 10.2.2.9 `CodigoPostalController` (REST)

**Ficheiro:** `controller/CodigoPostalController.java`

**Responsabilidade principal:** Fornecer consulta REST de códigos postais portugueses para preenchimento automático de localidade.

**URL base:** `/codigos-postais`

**Métodos:**

| Método | Anotação | URL | Parâmetros | Lógica | Resposta |
|--------|----------|-----|------------|--------|----------|
| `buscarPorCodigo()` | `@GetMapping` | `/codigos-postais/{codigoPostal}` | `@PathVariable String codigoPostal` | Procura código postal na base de dados. Se encontrado, retorna JSON com `codigoPostal` e `localidade`. Se não encontrado, retorna 404. | `ResponseEntity<Map<String,String>>` — JSON `{"codigoPostal": "...", "localidade": "..."}` |

**Aspetos técnicos relevantes:**
- Anotado com `@RestController` (respostas automáticas JSON)
- Consumido pelo JavaScript na página de Perfil via `fetch()` com debounce
- Código postal como String (formato "XXXX-XXX")
- Endpoint leve e específico para autocomplete de morada

---

## 10.2.3 Templates Thymeleaf

### 10.2.3.1 `login/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de dois painéis. O painel esquerdo contém branding visual da clínica com gradiente e ilustração SVG decorativa. O painel direito contém o formulário de login centralizado com logótipo, campos de entrada, mensagens de estado e hiperligações.

**Atributos Thymeleaf utilizados:**
- `th:action="@{/login}"` — ação do formulário para POST /login
- `th:if="${erroLogin != null}"` — exibe bloco de erro condicionalmente
- `th:text="${erroLogin}"` — conteúdo da mensagem de erro
- `th:if="${param.cadastro != null}"` — deteta parâmetro de sucesso de registo na URL
- `th:if="${param.redefinida != null}"` — deteta parâmetro de sucesso de redefinição
- `th:value="${emailInformado}"` — preserva email após tentativa falhada
- `th:href="@{/recuperar-senha}"` — link para recuperação
- `th:href="@{/cadastro}"` — link para registo
- `th:name="${_csrf.parameterName}"` + `th:value="${_csrf.token}"` — token CSRF manual

**Variáveis do Model consumidas:**
- `erroLogin` (String) — mensagem de erro de autenticação
- `emailInformado` (String) — email preenchido na tentativa anterior
- `param.cadastro`, `param.redefinida` — parâmetros de query string

**Formulários presentes:**
- `<form th:action="@{/login}" method="POST">` com campos `email` e `password`

**Fragmentos incluídos:** Nenhum (página autónoma).

**JavaScript associado:** Nenhum (apenas HTML + CSS + Thymeleaf).

**Exemplo esquemático:**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>...</head>
<body>
  <div class="login-container">
    <div class="login-branding">
      <!-- SVG logo + texto clínica -->
    </div>
    <div class="login-form">
      <img src="/images/logo.png" alt="Logo">
      <h2>Aceder à Plataforma</h2>
      <p>Introduza as suas credenciais para aceder ao sistema</p>
      <div th:if="${erroLogin != null}" class="alert-error">
        <span th:text="${erroLogin}">Mensagem de erro</span>
      </div>
      <div th:if="${param.cadastro != null}" class="alert-success">
        Conta criada com sucesso!
      </div>
      <form th:action="@{/login}" method="POST">
        <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
        <label>E-mail *</label>
        <input type="email" name="email" th:value="${emailInformado}" required>
        <label>Palavra-passe *</label>
        <input type="password" name="password" required>
        <button type="submit">Entrar</button>
      </form>
      <a th:href="@{/recuperar-senha}">Recuperar Palavra-passe</a>
      <a th:href="@{/cadastro}">Criar Conta</a>
    </div>
  </div>
</body>
</html>
```

---

### 10.2.3.2 `cadastro/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de duas colunas com painel informativo à esquerda e formulário de registo com efeito glass à direita. Contém validações visuais com classes CSS dinâmicas e modais legais inline (Termos e Privacidade) — não fragmentos.

**Atributos Thymeleaf utilizados:**
- `th:action="@{/cadastro}"` — ação do formulário
- `th:object="${cadastroForm}"` — objeto vinculado ao formulário
- `th:field="*{nome}"`, `*{email}`, `*{telefone}`, `*{password}`, `*{confirmPassword}`, `*{termos}` — vinculação bidirecional
- `th:classappend="${#fields.hasErrors('nome')} ? 'campo-erro'"` — classe CSS condicional para erro
- `th:errors="*{nome}"`, `*{email}`, etc. — mensagens de erro por campo
- `th:if="${#fields.hasErrors('nome')}"` — exibe erro condicionalmente
- `th:if="${#fields.hasGlobalErrors()}"` — erros globais (ex: passwords não coincidem)
- `th:each="err : ${#fields.globalErrors()}"` + `th:text="${err}"` — iteração de erros globais
- `th:href="@{/login}"` — link para login
- CSRF: `th:name` + `th:value` manual ou automático via `th:action`

**Variáveis do Model consumidas:**
- `cadastroForm` (CadastroForm) — objeto de formulário com valores e erros

**Formulários presentes:**
- `<form th:action="@{/cadastro}" th:object="${cadastroForm}" method="POST">` com 6 campos

**Fragmentos incluídos:** Nenhum (modais legais são HTML inline na página).

**JavaScript associado:** Funções para abrir/fechar modais legais (JavaScript inline).

**Exemplo esquemático:**

```html
<form th:action="@{/cadastro}" th:object="${cadastroForm}" method="POST"
      class="form-glass">
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">

  <div class="campo-grupo" th:classappend="${#fields.hasErrors('nome')} ? 'campo-erro'">
    <label>Nome Completo *</label>
    <input type="text" th:field="*{nome}" placeholder="Ana Silva Santos">
    <span th:if="${#fields.hasErrors('nome')}" th:errors="*{nome}" class="erro"></span>
  </div>

  <div class="campo-grupo">
    <label>E-mail *</label>
    <input type="email" th:field="*{email}" placeholder="ana@email.com">
    <span th:if="${#fields.hasErrors('email')}" th:errors="*{email}" class="erro"></span>
  </div>

  <!-- restantes campos: telefone, password, confirmPassword, termos -->

  <div th:if="${#fields.hasGlobalErrors()}" class="erros-globais">
    <p th:each="err : ${#fields.globalErrors()}" th:text="${err}"></p>
  </div>

  <button type="submit">Criar Conta</button>
</form>
```

---

### 10.2.3.3 `recuperar-senha/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de coluna única centrada com largura máxima de 480px. Apresenta formulário de email para pedido de recuperação ou mensagem de confirmação.

**Atributos Thymeleaf utilizados:**
- `th:if="${mensagemEnviada != null}"` — exibe mensagem de sucesso
- `th:text="${mensagemEnviada}"` — conteúdo da mensagem
- `th:if="${mensagemEnviada == null}"` — exibe formulário apenas se não houve envio
- `th:action="@{/recuperar-senha}"` — ação do formulário
- `th:href="@{/login}"` — link de voltar
- `th:name` + `th:value` para CSRF

**Variáveis do Model consumidas:**
- `mensagemEnviada` (String) — mensagem de confirmação (flash attribute)

**Formulários presentes:**
- `<form th:action="@{/recuperar-senha}" method="POST">` com campo `email`

**Fragmentos incluídos:** Nenhum.

---

### 10.2.3.4 `redefinir-senha/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de coluna única centrada. Exibe mensagem de erro de token ou formulário de redefinição com dois campos de password e campo oculto de token.

**Atributos Thymeleaf utilizados:**
- `th:if="${erroToken != null}"` — exibe bloco de erro de token
- `th:text="${erroToken}"` — texto do erro
- `th:href="@{/recuperar-senha}"` — link para novo pedido
- `th:if="${redefinirSenhaForm != null}"` — exibe formulário se token válido
- `th:action="@{/redefinir-senha}"` — ação do formulário
- `th:object="${redefinirSenhaForm}"` — objeto vinculado
- `th:field="*{token}"` — campo oculto
- `th:field="*{novaSenha}"`, `*{confirmarSenha}` — campos de password
- `th:classappend="${#fields.hasErrors('novaSenha')} ? 'has-error'"` — estilo de erro
- `th:errors="*{novaSenha}"` — mensagem de erro
- `th:unless="${#fields.hasErrors('novaSenha')}"` — exibe hint quando não há erro
- `th:href="@{/login}"` — link de voltar

**Variáveis do Model consumidas:**
- `erroToken` (String) — mensagem de token inválido/expirado
- `redefinirSenhaForm` (RedefinirSenhaForm) — objeto de formulário

**Formulários presentes:**
- `<form th:action="@{/redefinir-senha}" th:object="${redefinirSenhaForm}" method="POST">` com campos `token` (hidden), `novaSenha`, `confirmarSenha`

**JavaScript associado:** Alternância de visibilidade das passwords (olho) via JavaScript inline.

---

### 10.2.3.5 `consultas/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página principal da aplicação. Estrutura complexa com cabeçalho sticky, secção de herói saudação, sidebar de resumo, barra de filtros, lista de cards de consulta e fragmentos legais no footer.

**Atributos Thymeleaf utilizados (lista extensa):**
- `th:replace="~{fragments/modais-legais :: estilos}"` — inclui estilos de fragmentos
- `th:text="${nomeUtilizador}"` — saudação personalizada
- `th:text="${totalConsultas}"` — contagem total
- `th:text="${proximaVisita}"` — próxima consulta
- `th:text="${periodoConsultas}"` — período exibido
- `th:if="${param.marcada != null}"` — notificação de marcação bem-sucedida
- `th:if="${mensagemSucesso != null}"` + `th:text="${mensagemSucesso}"` — flash sucesso
- `th:if="${mensagemErro != null}"` + `th:text="${mensagemErro}"` — flash erro
- `th:action="@{/consultas}"` — formulário de filtros (GET)
- `th:each="estado : ${estadosConsulta}"` + `th:value="${estado.valor()}"` + `th:selected="..."` + `th:text="${estado.label()}"` — select de estado
- `th:selected="${periodoSelecionado == 'HOJE'}"` — select de período
- `th:each="dentista : ${dentistas}"` — select de dentista
- `th:each="tipo : ${tiposConsulta}"` — select de tipo
- `th:if="${mostrarFiltroPaciente}"` — campo de pesquisa (staff only)
- `th:value="${pesquisa}"` + `th:value="${dataInicio}"` + `th:value="${dataFim}"` — valores dos filtros
- `th:href="@{/consultas}"` — limpar filtros
- `th:if="${#lists.isEmpty(consultas)}"` — estado vazio
- `th:text="${filtrosAplicados} ? 'Nenhuma consulta encontrada' : 'Nenhuma consulta registada'"`
- `th:classappend="${consulta.borderClass()}"` — cor da borda do card
- `th:each="consulta : ${consultas}"` — iteração de consultas
- `th:text="${consulta.paciente()}"` + `${consulta.dentista()}` + `${consulta.procedimento()}` + `${consulta.data()}` + `${consulta.hora()}`
- `th:classappend="${consulta.badgeClass()}"` — cor do badge de estado
- `th:text="${consulta.status()}"` — texto do estado
- `th:if="${consulta.podeAlterar()}"` — exibe ações condicionalmente
- `th:href="@{/consultas/{id}/reagendar(id=${consulta.id()})}"` — link reagendar
- `th:href="@{/consultas/{id}/cancelar(id=${consulta.id()})}"` — link cancelar
- `th:href="@{/marcar-consulta}"` — botão nova marcação
- `th:block th:replace="~{fragments/modais-legais :: modais}"` — modais legais
- `th:block th:replace="~{fragments/modais-legais :: scripts}"` — scripts legais

**Variáveis do Model consumidas (25+):**
- `nomeUtilizador`, `totalConsultas`, `proximaVisita`, `periodoConsultas`
- `consultas` (List\<ConsultaView\>)
- `estadosConsulta` (List\<EstadoOpcao\>), `dentistas` (List\<DentistaOpcao\>), `tiposConsulta` (List\<String\>)
- `estadoSelecionado`, `periodoSelecionado`, `dataInicio`, `dataFim`, `dentistaSelecionadoId`, `pesquisa`, `tipoConsultaSelecionado`
- `mostrarFiltroPaciente`, `filtrosAplicados`
- `mensagemSucesso`, `mensagemErro` (flash attributes)
- `param.marcada` (query parameter)

**Formulários presentes:**
- `<form th:action="@{/consultas}" method="GET">` com filtros

**Fragmentos incluídos:**
- `fragments/modais-legais :: estilos`
- `fragments/modais-legais :: modais`
- `fragments/modais-legais :: scripts`

**JavaScript associado:** Navegação e interação são maioritariamente HTML/CSS; os modais legais têm JS para abrir/fechar.

---

### 10.2.3.6 `marcar-consulta/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de assistente de marcação em 3 passos. Estrutura de duas colunas: principal com seleções de dentista, data e hora; lateral com resumo. Utiliza JavaScript para carregamento dinâmico de horários.

**Atributos Thymeleaf utilizados:**
- `th:action="@{/marcar-consulta}"` — ação do formulário
- `th:if="${erroMarcacao != null}"` + `th:text="${erroMarcacao}"` — erro
- `th:each="dentista, iter : ${dentistas}"` — iteração de dentistas
- `th:value="${dentista.id()}"` + `th:checked="${dentista.id() == dentistaSelecionado}"` — rádio dentista
- `th:text="${dentista.nome()}"` + `th:text="${dentista.especialidade()}"` — dados do dentista
- `th:each="data : ${datas}"` — datas
- `th:value="${data.valor()}"` + `th:checked="${data.valor() == dataSelecionada}"` — rádio data
- `th:text="${data.diaSemana()}"` + `${data.diaMes()}` + `${data.mes()}`
- `th:each="horario : ${horarios}"` — horários
- `th:value="${horario}"` + `th:checked="${horario == horaSelecionada}"` — rádio hora
- `th:if="${#lists.isEmpty(horarios)}"` — sem horários
- `th:selected="${tipoSelecionado == 'Consulta Geral'}"` — select tipo
- `th:replace="~{fragments/modais-legais :: ...}"` — fragmentos

**Variáveis do Model consumidas:**
- `dentistas` (List\<DentistaOpcao\>), `datas` (List\<DataOpcao\>), `horarios` (List\<String\>)
- `dentistaSelecionado`, `dataSelecionada`, `horaSelecionada`, `tipoSelecionado`
- `erroMarcacao` (String)

**Formulários presentes:**
- `<form th:action="@{/marcar-consulta}" method="POST">` com `dentistaId` (radio), `data` (radio), `hora` (radio), `tipo` (select)

**JavaScript associado:**
```javascript
// Aquando seleção de dentista ou data:
fetch('/consultas/horarios-disponiveis?dentistaId=' + dentistaId + '&data=' + data)
  .then(r => r.json())
  .then(horarios => {
    // atualiza grelha de horários
  });
```

---

### 10.2.3.7 `reagendar-consulta/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de reagendamento com breadcrumb, cartão de consulta atual, formulário de nova data/hora e sidebar informativa. Inclui JavaScript para carregamento dinâmico de horários via `fetch()`.

**Atributos Thymeleaf utilizados:**
- `th:href="@{/consultas}"` — breadcrumb
- `th:text="${consulta.tipo}"` — tipo da consulta
- `th:if="${consulta.idDentista != null and consulta.idDentista.utilizador != null}"` — guard para dados do dentista
- `th:text="'Dr(a). ' + ${consulta.idDentista.utilizador.primeiroNome}"` — nome do dentista
- `th:text="${#temporals.format(consulta.dataHoraInicio, 'dd/MM/yyyy HH:mm')}"` — data/hora formatada
- `th:text="${consulta.status.descricao}"` — estado
- `th:action="@{/consultas/{id}/reagendar(id=${consulta.id})}"` — ação do formulário
- `th:if="${erroReagendamento != null}"` + `th:text="${erroReagendamento}"` — erro
- `th:each="data : ${datas}"` — datas disponíveis
- `th:checked="${data.valor() == dataSelecionada}"` — seleção padrão
- `th:each="horario : ${horarios}"` — horários
- `th:if="${#lists.isEmpty(horarios)}"` — sem disponibilidade
- `th:inline="javascript"` — para passar dados do modelo ao JavaScript
- `th:replace="~{fragments/modais-legais :: ...}"` — fragmentos

**Variáveis do Model consumidas:**
- `consulta` (entity Consulta)
- `datas` (List\<DataOpcao\>), `horarios` (List\<String\>), `dataSelecionada`
- `erroReagendamento` (String)

**Formulários presentes:**
- `<form th:action="@{/consultas/{id}/reagendar(id=${consulta.id})}" method="POST">` com `data` (radio) e `hora` (radio)

**JavaScript associado:**
```javascript
/*<![CDATA[*/
var consultaId = /*[[${consulta.id}]]*/ null;
/*]]>*/
// Aquando seleção de data:
fetch('/consultas/' + consultaId + '/horarios-disponiveis-reagendamento?data=' + data)
  .then(r => r.json())
  .then(horarios => { /* atualiza grelha */ });
```

---

### 10.2.3.8 `cancelar-consulta/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de cancelamento com breadcrumb, banner de aviso vermelho, cartão da consulta a cancelar, e formulário de motivo com rádios predefinidos e área de texto livre.

**Atributos Thymeleaf utilizados:**
- `th:replace="~{fragments/modais-legais :: estilos}"` — estilos legais
- `th:href="@{/consultas}"` — breadcrumb
- `th:href="@{/consultas/{id}/reagendar(id=${consulta.id})}"` — sugestão de reagendamento
- `th:text="${consulta.tipo}"` — tipo
- `th:if="${consulta.idDentista != null and consulta.idDentista.utilizador != null}"` — condicional dentista
- `th:text="'Dr(a). ' + ${consulta.idDentista.utilizador.primeiroNome} + ' ' + ${consulta.idDentista.utilizador.ultimoNome}"`
- `th:text="${consulta.dataHoraInicio != null} ? ${#temporals.format(consulta.dataHoraInicio.atZone(T(java.time.ZoneId).systemDefault()), 'dd/MM/yyyy')} : 'Sem data'"`
- `th:text="${consulta.dataHoraInicio != null} ? ${#temporals.format(consulta.dataHoraInicio.atZone(T(java.time.ZoneId).systemDefault()), 'HH:mm')} : '--:--'"`
- `th:action="@{/consultas/{id}/cancelar(id=${consulta.id})}"` — ação do formulário
- `th:object="${consulta}"` — (apenas para leitura, não para edição)
- `th:replace="~{fragments/modais-legais :: modais}"`, `:: scripts"`

**Variáveis do Model consumidas:**
- `consulta` (entity Consulta)

**Formulários presentes:**
- `<form th:action="@{/consultas/{id}/cancelar(id=${consulta.id})}" method="POST">` com campo oculto `motivo-final` (preenchido por JS) e visualmente com 4 rádios + textarea

**JavaScript associado:**
- Combina rádio selecionado + textarea num campo oculto `motivo-final`
- Contador de caracteres no textarea (max 500)
- Ativa/desativa botão de confirmação conforme validade do motivo

---

### 10.2.3.9 `perfil/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de perfil com layout de duas colunas. Coluna lateral com avatar de iniciais, nome e logout. Coluna principal com formulário de edição em 3 secções.

**Atributos Thymeleaf utilizados:**
- `th:replace="~{fragments/modais-legais :: estilos}"` — estilos
- `th:text="${nomeCompleto}"` — nome do utilizador
- `th:text="${tipoUtilizador}"` — tipo (PACIENTE, RECEPCIONISTA, etc.)
- `th:if="${param.atualizado != null}"` — notificação de sucesso
- `th:action="@{/logout}"` — formulário de logout
- `th:action="@{/perfil}"` — formulário de edição
- `th:object="${perfilForm}"` — objeto vinculado
- `th:field="*{primeiroNome}"`, `*{ultimoNome}`, `*{dataNascimento}`, `*{nif}`, `*{email}`, `*{telemovel}`, `*{telefone}`, `*{rua}`, `*{numeroPorta}`, `*{codigoPostal}`, `*{localidade}` — todos os campos
- `th:classappend="${#fields.hasErrors('primeiroNome')} ? 'has-error'"` — erro condicional
- `th:errors="*{primeiroNome}"`, etc. — mensagens de erro
- `th:if="${#fields.hasErrors('primeiroNome')}"` — exibição condicional de erro
- `th:for="${#ids.next('primeiroNome')}"` — vinculação label-input com IDs únicos
- `th:if="${#fields.hasGlobalErrors()}"` — erros globais

**Variáveis do Model consumidas:**
- `perfilForm` (PerfilForm) — formulário com valores
- `nomeCompleto` (String), `tipoUtilizador` (String)
- `param.atualizado` — flag de sucesso

**Formulários presentes:**
- `<form th:action="@{/logout}" method="POST">` (logout)
- `<form th:action="@{/perfil}" th:object="${perfilForm}" method="POST">` (edição)

**JavaScript associado:**
- Pesquisa automática de localidade: `fetch('/codigos-postais/' + cp)` com debounce
- Formatação automática de código postal (inserção de hífen)

---

### 10.2.3.10 `faturas/index.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de listagem de faturas (staff). Cabeçalho sticky, título, barra de filtros estática, tabela de faturas com colunas, cartões informativos laterais.

**Atributos Thymeleaf utilizados:**
- `th:replace="~{fragments/modais-legais :: estilos}"`
- `th:if="${#lists.isEmpty(faturas)}"` — estado vazio
- `th:each="fatura : ${faturas}"` — iteração
- `th:text="${fatura.tratamento()}"`, `${fatura.dentista()}`, `${fatura.dataEmissao()}`, `${fatura.valor()}`
- `th:classappend="${fatura.classeEstado()}"` — classe CSS do badge
- `th:classappend="${fatura.pontoEstado()}"` — ponto colorido
- `th:text="${fatura.estado()}"` — texto do estado
- `th:if="${fatura.pdfDisponivel()}"` — botão download condicional
- `th:href="@{|/faturas/${fatura.id()}/download|}"` — link de download
- `th:unless="${fatura.pdfDisponivel()}"` — "PDF indisponível"
- `th:replace="~{fragments/modais-legais :: modais}"`, `:: scripts`

**Variáveis do Model consumidas:**
- `faturas` (List\<FaturaView\>)

---

### 10.2.3.11 `paciente/faturas.html`

**Tipo:** Página completa

**Descrição da estrutura HTML:** Página de listagem de faturas para paciente. Layout responsivo em grelha (desktop) ou blocos (mobile). Cada item exibe descrição, número, data, valor, badge de estado e ações de download.

**Atributos Thymeleaf utilizados:**
- `th:replace="~{fragments/modais-legais :: estilos}"`
- `th:if="${#lists.isEmpty(faturas)}"` — vazio
- `th:if="${!#lists.isEmpty(faturas)}"` — tem dados
- `th:each="fatura : ${faturas}"` — iteração
- `th:text="${fatura.descricao()}"`, `${fatura.id()}`, `${fatura.dataEmissao()}`, `${fatura.valor()}`
- `th:classappend="${fatura.corBadge()}"` — badge colorido
- `th:text="${fatura.estadoLabel()}"` — texto do estado em português
- `th:if="${fatura.podeBaixar()}"` — download disponível
- `th:href="@{|/paciente/faturas/${fatura.id()}/download|}"` — link download
- `th:if="${fatura.estadoCodigo() == 'PAGA' and !fatura.podeBaixar()}"` — PDF em processamento
- `th:if="${fatura.estadoCodigo() != 'PAGA'}"` — a aguardar pagamento
- `th:replace="~{fragments/modais-legais :: modais}"`, `:: scripts`

**Variáveis do Model consumidas:**
- `faturas` (List\<FaturaView\> — versão paciente)
- `nomeUtilizador`

---

### 10.2.3.12 `fragments/modais-legais.html`

**Tipo:** Fragmento reutilizável

**Descrição da estrutura HTML:** Biblioteca de fragmentos Thymeleaf que define três fragmentos: `estilos` (CSS para modais legais), `modais` (três modais HTML: Termos de Serviço, Política de Privacidade e Contactos), e `scripts` (JavaScript para abrir/fechar modais).

**Fragmentos definidos:**
- `th:fragment="estilos"` — bloco `<style>` com classes CSS para modais, animações, cards legais
- `th:fragment="modais"` — três `<div id="modalTermos">`, `<div id="modalPrivacidade">`, `<div id="modalContactos">` com conteúdos de texto legal
- `th:fragment="scripts"` — funções JavaScript `abrirModalLegal(id)`, `fecharModalLegal(id)`, `fecharAoClicarFora(event, id)`, listener de tecla Escape

**Como é incluído:**
```html
<!-- No <head> de cada página que usa modais -->
<th:block th:replace="~{fragments/modais-legais :: estilos}"></th:block>

<!-- No footer de cada página -->
<th:block th:replace="~{fragments/modais-legais :: modais}"></th:block>
<th:block th:replace="~{fragments/modais-legais :: scripts}"></th:block>
```

**Variáveis do Model consumidas:** Nenhuma (conteúdo estático).

---

## 10.2.4 Estilização e Design System

### 10.2.4.1 Paleta de Cores e Identidade Visual

A identidade visual da aplicação web é construída sobre uma paleta de cores teal/verde-azulado institucional, transmitindo seriedade, confiança e ambiente clínico. As cores são maioritariamente aplicadas através de classes CSS utilitárias definidas via Tailwind CSS (carregado por CDN) com configuração customizada.

**Cores primárias (Tailwind config):**

| Token | Valor | Utilização |
|-------|-------|------------|
| `primary-50` | `#ecfdf5` | Fundos muito claros, hover states |
| `primary-100` | `#d1fae5` | Backgrounds de badges sucesso |
| `primary-200` | `#a7f3d0` | — |
| `primary-300` | `#6ee7b7` | Elementos decorativos |
| `primary-400` | `#34d399` | Acentos, links, ícones |
| `primary-500` | `#10b981` | Botões primários, bordas de foco |
| `primary-600` | `#059669` | Hover de botões, cabeçalhos de tabela |
| `primary-700` | `#047857` | Cabeçalhos, títulos, nav ativa |
| `primary-800` | `#065f46` | Texto escuro sobre fundo claro |
| `primary-900` | `#064e3b` | Máximo contraste |

**Cores de estado (feedback visual):**

| Token | Valor | Utilização |
|-------|-------|------------|
| `success` | `#10b981` | Consultas CONCLUIDA, Faturas PAGA, mensagens de sucesso |
| `warning` | `#f59e0b` | Consultas PENDENTE/AGENDADA, alertas |
| `error` / `danger` | `#ef4444` | Consultas CANCELADA, erros de validação, avisos críticos |
| `info` | `#3b82f6` | Consultas EM_CONSULTA, badges informativos |

**Cores neutras:**

| Token | Valor | Utilização |
|-------|-------|------------|
| `neutral-50` | `#f8fafc` | Fundo de página |
| `neutral-100` | `#f1f5f9` | Fundo de cartões, tabelas |
| `neutral-200` | `#e2e8f0` | Bordas subtis, separadores |
| `neutral-300` | `#cbd5e1` | Bordas de inputs, placeholders |
| `neutral-400` | `#94a3b8` | Texto secundário, ícones desativados |
| `neutral-500` | `#64748b` | Texto terciário, labels |
| `neutral-600` | `#475569` | Texto corporal |
| `neutral-700` | `#334155` | Títulos de secção |
| `neutral-800` | `#1e293b` | Títulos principais |
| `neutral-900` | `#0f172a` | Cabeçalhos de página |

**Gradientes:** O fundo da página de login usa `bg-gradient-to-br from-primary-50 via-white to-primary-100`. Cartões e botões primários usam `bg-gradient-to-r from-primary-500 to-primary-600`.

### 10.2.4.2 Tipografia

**Famílias tipográficas (Google Fonts via CDN):**

- **Plus Jakarta Sans** — fonte principal para títulos e corpo
- **Manrope** — fonte alternativa para texto de interface e dados tabulares
- **Material Symbols** — iconografia (ícones de documentos, navegação, ações)

**Hierarquia tipográfica:**

| Elemento | Família | Peso | Tamanho (classe Tailwind) |
|----------|---------|------|--------------------------|
| Título de página (h1) | Plus Jakarta Sans | 700 (bold) | `text-2xl` ou `text-3xl` (32–36px) |
| Título de secção (h2) | Plus Jakarta Sans | 600 (semibold) | `text-xl` (24px) |
| Subtítulo / descrição | Plus Jakarta Sans | 400 (regular) | `text-sm` (14px) |
| Navegação (nav links) | Plus Jakarta Sans | 500 (medium) | `text-sm` (14px) |
| Nome do utilizador | Plus Jakarta Sans | 600 (semibold) | `text-base` (16px) |
| Texto corporal | Manrope | 400 (regular) | `text-sm` (14px) |
| Dados de tabela | Manrope | 400 (regular) | `text-sm` (14px) |
| Rótulos de formulário | Plus Jakarta Sans | 500 (medium) | `text-sm` (14px) |
| Badges / estado | Plus Jakarta Sans | 600 (semibold) | `text-xs` (12px) |
| Valores monetários | Manrope | 600 (semibold) | `text-base` (16px) |

### 10.2.4.3 Componentes Reutilizáveis

A aplicação utiliza o Tailwind CSS (CDN) como framework utilitário. Não existem classes CSS personalizadas num ficheiro dedicado — todo o estilo é aplicado através de classes utilitárias Tailwind nos templates HTML. No entanto, alguns padrões de componentes são reutilizados consistentemente:

**Botão Primário:**
```html
<button class="bg-gradient-to-r from-primary-500 to-primary-600 text-white font-medium
               py-3 px-8 rounded-xl hover:shadow-lg hover:from-primary-600
               hover:to-primary-700 transition-all duration-300 disabled:opacity-50
               disabled:cursor-not-allowed">
  Entrar
</button>
```

**Cartão / Card:**
```html
<div class="bg-white rounded-2xl shadow-sm border border-neutral-100 p-6">
  <!-- conteúdo -->
</div>
```

**Campo de formulário:**
```html
<div class="mb-4">
  <label class="block text-sm font-medium text-neutral-700 mb-1">E-mail *</label>
  <input type="email" class="w-full px-4 py-3 rounded-xl border border-neutral-200
                focus:border-primary-500 focus:ring-2 focus:ring-primary-200
                transition-all duration-200 outline-none">
</div>
```

**Badge de estado:**
```html
<span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-semibold
             bg-primary-100 text-primary-700">
  CONCLUÍDA
</span>
```

**Navegação (header sticky):**
```html
<nav class="sticky top-0 bg-white/80 backdrop-blur-md border-b border-neutral-100 z-50">
  <div class="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">
    <!-- logo + links + perfil -->
  </div>
</nav>
```

**Tabela de dados:**
```html
<table class="w-full">
  <thead class="bg-primary-50">
    <tr><th class="px-4 py-3 text-left text-sm font-medium text-primary-800">Coluna</th></tr>
  </thead>
  <tbody class="divide-y divide-neutral-100">
    <tr class="hover:bg-neutral-50 transition-colors"><td class="px-4 py-3">Valor</td></tr>
  </tbody>
</table>
```

### 10.2.4.4 Responsividade e Breakpoints

A aplicação utiliza os breakpoints padrão do Tailwind CSS:

| Breakpoint | Largura mínima | Alvo |
|------------|---------------|------|
| `sm` | 640px | Telemóveis grandes |
| `md` | 768px | Tablets |
| `lg` | 1024px | Ecrãs médios / portáteis |
| `xl` | 1280px | Ecrãs grandes (desktop) |
| `2xl` | 1536px | Ecrãs muito grandes |

**Estratégia de responsividade por página:**

- **Login / Recuperação / Redefinição:** Layout de dois painéis colapsa para um único painel centrado em `<lg`. Formulário ocupa 100% em `<md`.
- **Consultas:** Sidebar (280px) é ocultada em `<lg`. Filtros empilham verticalmente em `<md`. Cards de consulta simplificam informação em `<sm`.
- **Marcar consulta:** Grelha de dentistas: 3 colunas em `xl`, 2 em `lg`, 1 em `<md`. Grelha de datas/horários: 5 colunas em `xl`, 3 em `md`, 2 em `<sm`.
- **Perfil:** Layout de duas colunas colapsa para coluna única em `<lg`. O avatar e resumo passam para o topo.
- **Faturas (paciente):** Grelha de itens adapta de 1 coluna em telemóvel para colunas de dados em desktop (layout tableless com CSS grid).

### 10.2.4.5 Estados e Feedback Visual

**Estados de campos de formulário:**

| Estado | Aparência |
|--------|-----------|
| Normal | Borda `neutral-200`, fundo branco |
| Foco | Borda `primary-500`, ring `ring-2 ring-primary-200` |
| Erro | Borda `error` (vermelho), classe `campo-erro` / `has-error` adicionada via `th:classappend` |
| Sucesso (após validação) | Borda `success` (verde) — não implementado atualmente |
| Desabilitado | Opacidade 50%, cursor not-allowed |

**Estados de botões:**

| Estado | Aparência |
|--------|-----------|
| Normal | Gradiente primary-500→600, texto branco |
| Hover | Gradiente primary-600→700, sombra `shadow-lg` |
| Ativo / Click | Escurecimento adicional |
| Desabilitado | Opacidade 50%, cursor not-allowed, sem hover |
| Carregamento | Não implementado (sem spinner) |

**Feedback visual de ações:**

| Ação | Feedback |
|------|----------|
| Login sucesso | Redirecionamento para `/consultas` |
| Login falha | Mensagem de erro `erroLogin` no topo do formulário |
| Registo sucesso | Redirecionamento para `/login?cadastro=sucesso` com banner verde |
| Marcação sucesso | Redirecionamento para `/consultas?marcada=sucesso` com banner verde |
| Operação (cancelar/reagendar) sucesso | Flash message `mensagemSucesso` no topo da página de consultas |
| Operação erro | Flash message `mensagemErro` no topo da página |
| Erro de validação | Mensagens individuais por campo com classe `has-error` e texto vermelho |
| Erro de token (recuperação) | Mensagem de erro com link para novo pedido |

**Badges de estado de consulta (cores por estado):**

| Estado | Classe Tailwind (exemplo) | Cor aproximada |
|--------|---------------------------|----------------|
| AGENDADA | `bg-amber-100 text-amber-700` | Amarelo |
| CONFIRMADA | `bg-blue-100 text-blue-700` | Azul |
| EM_ESPERA | `bg-purple-100 text-purple-700` | Roxo |
| EM_CONSULTA | `bg-cyan-100 text-cyan-700` | Ciano |
| CONCLUIDA | `bg-green-100 text-green-700` | Verde |
| CANCELADA | `bg-red-100 text-red-700` | Vermelho |
| FATURADA | `bg-primary-100 text-primary-700` | Teal |

---

## 10.2.5 Infraestrutura e Configuração

### 10.2.5.1 Estrutura e Organização do Projeto

O projeto segue a estrutura padrão Maven/Gradle para aplicações Spring Boot com organização por camadas:

```
Clinica---Projeto-web/
├── build.gradle                          # Configuração de build (Gradle)
├── settings.gradle                       # Nome do projeto
├── gradlew / gradlew.bat                 # Wrapper do Gradle
├── gradle/wrapper/                       # Ficheiros do wrapper
└── src/
    └── main/
        ├── java/
        │   ├── app/
        │   │   ├── MainApplication.java        # Entry point Spring Boot
        │   │   ├── TestWebApplication.java     # Entry point alternativo (teste sem BD)
        │   │   └── SecurityConfig.java         # Configuração de segurança
        │   ├── controller/
        │   │   ├── LoginController.java
        │   │   ├── CadastroController.java
        │   │   ├── ConsultasController.java
        │   │   ├── MarcarConsultaController.java
        │   │   ├── PerfilController.java
        │   │   ├── FaturasController.java
        │   │   ├── FaturaDownloadController.java
        │   │   ├── RecuperacaoSenhaController.java
        │   │   └── CodigoPostalController.java
        │   ├── bll/                             # Service layer
        │   │   ├── ConsultaService.java
        │   │   ├── PacienteService.java
        │   │   ├── UtilizadorService.java
        │   │   ├── FaturaService.java
        │   │   ├── PagamentoService.java
        │   │   ├── RecuperacaoSenhaService.java
        │   │   ├── DentistaService.java
        │   │   ├── SeguroService.java
        │   │   └── ... (outros services)
        │   ├── dal/                             # Repository layer (JPA)
        │   │   ├── ConsultaRepository.java
        │   │   ├── PacienteRepository.java
        │   │   ├── UtilizadorRepository.java
        │   │   └── ... (outros repositórios)
        │   └── model/                           # Entity classes
        │       ├── Consulta.java
        │       ├── Paciente.java
        │       ├── Utilizador.java
        │       ├── Fatura.java
        │       ├── Atendimento.java
        │       ├── Seguro.java
        │       ├── Dentista.java
        │       └── ... (outras entidades + enums)
        └── resources/
            ├── application.properties           # Configuração da aplicação
            ├── templates/                       # Thymeleaf templates
            │   ├── login/index.html
            │   ├── cadastro/index.html
            │   ├── consultas/index.html
            │   ├── marcar-consulta/index.html
            │   ├── reagendar-consulta/index.html
            │   ├── cancelar-consulta/index.html
            │   ├── perfil/index.html
            │   ├── faturas/index.html
            │   ├── paciente/faturas.html
            │   ├── recuperar-senha/index.html
            │   ├── redefinir-senha/index.html
            │   └── fragments/
            │       └── modais-legais.html
            ├── static/
            │   └── images/
            │       └── logo.png
            └── db/
                └── init/
                    └── postgresql.sql           # Inicialização do schema
```

**Separação em camadas:**

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| Controller | `controller` | Receber pedidos HTTP, validar input, chamar serviços, retornar vistas |
| Service (BLL) | `bll` | Lógica de negócio, coordenação de repositórios, transações |
| Repository (DAL) | `dal` | Acesso a dados JPA, queries |
| Model | `model` | Entidades JPA, enums, relações |
| App/Config | `app` | Configuração Spring, segurança, entry points |

### 10.2.5.2 Roteamento e Mapeamento de URLs

A aplicação utiliza o mapeamento padrão do Spring MVC (`@RequestMapping`, `@GetMapping`, `@PostMapping`) para definir rotas. Não existe um ficheiro de configuração de rotas centralizado — cada controller define os seus próprios mapeamentos.

**Mapa completo de URLs:**

| Método | URL | Controller | Descrição |
|--------|-----|------------|-----------|
| GET | `/` | `LoginController` | Página de login |
| GET | `/login` | `LoginController` | Página de login |
| POST | `/login` | `LoginController` | Autenticação |
| GET | `/dashboard` | `LoginController` | Redireciona para `/consultas` |
| POST | `/logout` | `LoginController` | Logout |
| GET | `/cadastro` | `CadastroController` | Formulário de registo |
| POST | `/cadastro` | `CadastroController` | Processar registo |
| GET | `/consultas` | `ConsultasController` | Lista de consultas com filtros |
| GET | `/consultas/{id}/cancelar` | `ConsultasController` | Página de cancelamento |
| POST | `/consultas/{id}/cancelar` | `ConsultasController` | Confirmar cancelamento |
| GET | `/consultas/{id}/reagendar` | `ConsultasController` | Página de reagendamento |
| POST | `/consultas/{id}/reagendar` | `ConsultasController` | Confirmar reagendamento |
| GET | `/consultas/{id}/horarios-disponiveis-reagendamento` | `ConsultasController` | JSON horários (reagendar) |
| GET | `/marcar-consulta` | `MarcarConsultaController` | Página de marcação |
| POST | `/marcar-consulta` | `MarcarConsultaController` | Confirmar marcação |
| GET | `/consultas/horarios-disponiveis` | `MarcarConsultaController` | JSON horários (marcar) |
| GET | `/perfil` | `PerfilController` | Página de perfil |
| POST | `/perfil` | `PerfilController` | Atualizar perfil |
| GET | `/faturas` | `FaturasController` | Lista de faturas (staff) |
| GET | `/faturas/{id}/download` | `FaturasController` | Download PDF fatura |
| GET | `/paciente/faturas` | `FaturaDownloadController` | Lista de faturas (paciente) |
| GET | `/paciente/faturas/{id}/download` | `FaturaDownloadController` | Download PDF fatura (paciente) |
| GET | `/recuperar-senha` | `RecuperacaoSenhaController` | Formulário de recuperação |
| POST | `/recuperar-senha` | `RecuperacaoSenhaController` | Pedido de recuperação |
| GET | `/redefinir-senha` | `RecuperacaoSenhaController` | Formulário de redefinição |
| POST | `/redefinir-senha` | `RecuperacaoSenhaController` | Confirmar redefinição |
| GET | `/codigos-postais/{codigoPostal}` | `CodigoPostalController` | JSON dados de código postal |

**Padrão de nomenclatura de URLs:**
- URLs de páginas: substantivos no plural ou ação (`/consultas`, `/marcar-consulta`, `/perfil`)
- URLs de recursos aninhados: `/{recurso}/{id}/{acao}`
- URLs de API REST: `/consultas/horarios-disponiveis` (convenção, não REST puro)
- URLs específicas de paciente: `/paciente/faturas`

### 10.2.5.3 Gestão de Sessão e Autenticação

A aplicação não utiliza o mecanismo de autenticação do Spring Security (não há `AuthenticationManager`, `UserDetailsService`, filtros de segurança comuns). Em vez disso, a autenticação é gerida manualmente através da `HttpSession` com verificação em cada controller.

**Mecanismo:**

1. **Login:** `LoginController.autenticar()` invoca `utilizadorService.autenticar(email, password)` que verifica a password contra o hash BCrypt guardado. Se válido, guarda na sessão:
   - `utilizadorId` (Integer) — ID do utilizador autenticado
   - `utilizadorNome` (String) — nome completo para exibição
   - `utilizadorTipo` (String) — "PACIENTE", "RECEPCIONISTA", "DENTISTA", etc.
   - `utilizadorNif` (String) — NIF do utilizador

2. **Verificação em controllers:** Cada método de controller que requer autenticação verifica:
   ```java
   Integer utilizadorId = (Integer) session.getAttribute("utilizadorId");
   if (utilizadorId == null) {
       return "redirect:/login";
   }
   ```

3. **Restrição por tipo:** O `ConsultasController` e o `MarcarConsultaController` verificam o tipo de utilizador:
   ```java
   String tipo = (String) session.getAttribute("utilizadorTipo");
   if (!"PACIENTE".equals(tipo)) {
       model.addAttribute("erroMarcacao", "Apenas pacientes podem marcar consultas");
   }
   ```

4. **Logout:** `LoginController.logout()` invoca `session.invalidate()` e redireciona para `/login`.

5. **SecurityConfig:** O `SecurityConfig` atual permite todas as requisições (`permitAll()`) em todas as URLs, com CSRF ativo (padrão Spring Security). Isto significa que a única proteção real é a verificação manual de sessão nos controllers.

**Configuração de segurança (SecurityConfig):**
```java
@Bean
public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
}
```

### 10.2.5.4 Integração entre Camadas (Controller → Service → Repository)

A arquitetura segue o padrão em camadas do Spring Boot:

**Fluxo típico de uma requisição:**

```
HTTP Request
    ↓
Controller (valida input, chama serviço)
    ↓
Service (lógica de negócio, orquestração)
    ↓
Repository (acesso a dados JPA)
    ↓
Database (PostgreSQL)
    ↓
Repository → Service → Controller
    ↓
Model (popula atributos)
    ↓
View (Thymeleaf template renderizado)
    ↓
HTTP Response (HTML)
```

**Exemplo concreto — Fluxo de consultas com filtros:**

1. `GET /consultas?estado=CONCLUIDA&periodo=HOJE`
2. `ConsultasController.consultas()` recebe o pedido
3. Controller verifica sessão → obtém `utilizadorId` e `utilizadorTipo`
4. Controller invoca `consultaService.buscarConsultasFiltradas(...)` com os parâmetros
5. Service aplica lógica de negócio: constrói especificações JPA, aplica filtros por estado/período/dentista/tipo/pesquisa, ordena por data/hora
6. Service invoca `consultaRepository.findAll(specification, sort)` 
7. Repository JPA executa a query na base de dados PostgreSQL
8. Service retorna lista de entidades `Consulta`
9. Controller converte entidades para `ConsultaView` (DTO de apresentação)
10. Controller adiciona `List<ConsultaView>`, selects de filtro, e metadados ao `Model`
11. Template `consultas/index.html` renderiza o HTML com Thymeleaf

**Injeção de dependências:**
- Todos os controllers e services usam `@Autowired` (injeção por construtor implícita no Spring Boot 3)
- `MainApplication` define `@ComponentScan({"app","bll","dal","model","controller"})` e `@EnableJpaRepositories("dal")`
- `MainApplication` também define `@EntityScan("model")`

### 10.2.5.5 Configuração do Ambiente e Build (Gradle / application.properties)

**Build (build.gradle):**

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.2.5'
    id 'io.spring.dependency-management' version '1.1.4'
}

group = 'com.clinica'
version = '0.0.1-SNAPSHOT'

java { sourceCompatibility = '21' }

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    runtimeOnly 'org.postgresql:postgresql'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// Tarefa customizada para testar sem base de dados
tasks.register('bootRunTestWeb', JavaExec) {
    classpath = sourceSets.main.runtimeClasspath
    mainClass = 'app.TestWebApplication'
}
```

**Application Properties (application.properties):**

```properties
spring.application.name=ClinicaDentaria

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/bd.clinica
spring.datasource.username=postgres
spring.datasource.password=senha
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate naming
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy

# SQL initialization
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:db/init/postgresql.sql
spring.sql.init.separator=@@

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Mail (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:}
spring.mail.password=${MAIL_PASSWORD:}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.default-encoding=UTF-8

# Application URLs
app.base-url=http://localhost:8080
app.faturas-dir=C:/Users/jenni/intelijProjetos/clinica/uploads/faturas
```

**Estratégia de inicialização:**
- `ddl-auto=update` — Hibernate atualiza o schema automaticamente com base nas entidades JPA
- `spring.sql.init.mode=always` — executa o script SQL de inicialização em cada arranque
- Script `db/init/postgresql.sql` contém a definição inicial do schema PostgreSQL (separador `@@`)
- `MainApplication` implementa `CommandLineRunner` para semear dados de teste (utilizadores, pacientes, dentistas, consultas, faturas) no arranque se `app.skipSeed` não for true

### 10.2.5.6 Proteção CSRF e Segurança

**CSRF (Cross-Site Request Forgery):**
- O Spring Security está incluído como dependência, mas configurado para permitir todas as requisições (`permitAll()`)
- No entanto, a proteção CSRF está ativa por padrão no Spring Security
- Em cada formulário POST, o token CSRF é injetado manualmente:
  ```html
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
  ```
- Alternativamente, formulários com `th:action` recebem o token automaticamente quando o Spring Security está configurado

**Password storage:**
- `BCryptPasswordEncoder` configurado como bean em `SecurityConfig`
- Utilizado pelo `UtilizadorService` para codificar palavras-passe antes de persistir
- Utilizado para verificar passwords durante a autenticação (`matches(rawPassword, encodedPassword)`)

**Segurança por obscuridade vs. segurança real:**
- A verificação de sessão é feita manualmente em cada controller, não por filtros globais
- Não existe diferenciação de roles a nível de segurança (apenas verificação `"PACIENTE".equals(tipo)`)
- URLs como `/faturas` não distinguem entre staff e paciente — ambos veem a lista completa (embora o paciente devesse ver apenas as suas)
- O `FaturaDownloadController` implementa verificação de propriedade de fatura (`buscarPorIdEPaciente`)

### 10.2.5.7 Tratamento de Exceções e Validações

**Validação server-side (Bean Validation):**

A aplicação utiliza `spring-boot-starter-validation` com anotações `jakarta.validation`:

| DTO | Anotações principais | Validações customizadas |
|-----|---------------------|------------------------|
| `CadastroForm` | `@NotBlank`, `@Email`, `@Pattern`, `@AssertTrue` | `@ValidPassword`, `@ValidTelefonePortugues`, `@PasswordMatches` |
| `PerfilForm` | `@NotBlank`, `@Size`, `@Pattern`, `@Past`, `@Email` | `@ValidNif`, `@ValidTelefonePortugues` |
| `RedefinirSenhaForm` | `@NotBlank` | `@ValidPassword` |

**Validações customizadas (implementadas como anotações + validators):**

| Anotação | Campos | Regra |
|----------|--------|-------|
| `@ValidPassword` | password | Mínimo 8 caracteres, pelo menos 1 maiúscula, 1 minúscula, 1 dígito, 1 caractere especial |
| `@ValidTelefonePortugues` | telefone, telemovel | 9 dígitos, começa por 9 (telemóvel) ou 2 (linha fixa) |
| `@ValidNif` | nif | Validação dos dígitos de controlo do NIF português |
| `@PasswordMatches` | password + confirmPassword | Os dois campos devem ser iguais (validação a nível de classe) |

**Tratamento de erros nos controllers:**

| Controller | Estratégia |
|------------|-----------|
| `CadastroController` | Erros de validação retornam à vista com `BindingResult` — campos inválidos exibem mensagens individuais |
| `PerfilController` | Erros de validação retornam à vista com `BindingResult` — `th:errors` por campo |
| `ConsultasController` | Exceções de serviço capturadas em try-catch, convertidas em flash attributes (`mensagemSucesso`/`mensagemErro`) |
| `RecuperacaoSenhaController` | Erros de token exibidos como `erroToken` no modelo; erros de validação via `BindingResult` |
| `FaturaDownloadController` | Exceção `FaturaAcessoNegadoException` (não tratada globalmente — resultaria em 500) |

**Tratamento de exceções global:** Não existe um `@ControllerAdvice` global. As exceções não capturadas resultam na página de erro padrão do Spring Boot (whitelabel). Isto é uma oportunidade de melhoria para produção.

**Mensagens flash (RedirectAttributes):**
- Utilizadas para passar mensagens de sucesso/erro entre redirects
- `RedirectAttributes.addFlashAttribute("mensagemSucesso", "...")`
- Lidas no template via `th:if="${mensagemSucesso != null}"`
- Usadas em: `ConsultasController` (cancelar, reagendar)

---

# Fim do Relatório Técnico
