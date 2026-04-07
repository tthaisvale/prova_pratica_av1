Esse projeto foi desenvolvido a partir do código base da branch UnitCompleto, que já tinha uma API REST simples para gerenciamento de itens, com suporte a JSON e XML.

O objetivo aqui foi melhorar a parte de testes, cobrindo não só os cenários de sucesso, mas também os casos de erro que podem acontecer na aplicação.

O QUE FOI FEITO:
-> Validação de regras de negócio

Foram adicionadas algumas validações importantes, como:
Não permitir criar item com:
Nome vazio ou nulo
Descrição vazia ou nula

Quando isso acontece, é lançada uma exceção personalizada:
InvalidItemDataException
-----

SOBRE ERROS:
Foi implementado um tratamento global de exceções, para garantir que a API responda corretamente:

-> 400 (Bad Request) → quando os dados estão inválidos;
-> 404 (Not Found) → quando o item não existe.
-----

ENDPOINTS CRIADOS:
-> Contar itens
GET /api/items/count
Retorna a quantidade total de itens cadastrados

-> Buscar por nome
GET /api/items/search?name=...
Retorna itens com base no nome informado
-----
TESTES IMPLEMENTADOS:
-> Casos de sucesso:
Criar item com dados válidos
Buscar item existente
Atualizar item
Deletar item
Testar os novos endpoints

-> Casos de erro:
Criar item com nome ou descrição inválidos
JSON inválido
Buscar item que não existe
Atualizar item inexistente
Deletar item inexistente
-----
RESULTADOS:
-> Os erros são encontrados mais cedo
-> O código ficou mais seguro
-> A API ficou mais confiável
-> A cobertura de testes aumentou bastante

No geral, o projeto ficou mais completo, com testes cobrindo tanto os casos positivos quanto os negativos, além da adição de novos endpoints. Isso ajuda a garantir que a API funcione corretamente em diferentes situações.
