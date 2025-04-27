# Jgol - Linguagem de Programação e IDE

Jgol é um dialeto de Portugol, dinamicamente tipado e orientado a objetos. Projetado para ser uma linguagem de programação educacional com sintaxe em português, facilitando o aprendizado de programação para falantes de português.

## Instalação

Para executar programas Jgol, você precisa baixar e instalar o IDE Jgol:

1. Baixe a versão mais recente do Jgol IDE
2. Execute o instalador e siga as instruções
3. Inicie o Jgol IDE para começar a programar

## Características da Linguagem

- Sintaxe em português
- Tipagem dinâmica
- Orientação a objetos
- Suporte a arrays multidimensionais
- Estruturas de controle de fluxo
- Funções e recursão

## Sintaxe Básica

### Variáveis

As variáveis são declaradas usando a palavra-chave `variavel`:

```
variavel nome = "João";
variavel idade = 25;
variavel altura = 1.75;
variavel ativo = verdadeiro;
```

### Operadores

Jgol suporta os seguintes operadores:

- Aritméticos: `+`, `-`, `*`, `/`
- Comparação: `==`, `!=`, `<`, `>`, `<=`, `>=`
- Lógicos: `e`, `ou`
- Atribuição: `=`

### Estruturas de Controle

#### Condicional (Se-Senão)

```
se(condição) {
    // código a ser executado se a condição for verdadeira
} senao {
    // código a ser executado se a condição for falsa
}
```

Exemplo:

```
variavel idade = 18;

se(idade >= 18) {
    escreva "Você é maior de idade";
} senao {
    escreva "Você é menor de idade";
}
```

#### Escolha-Caso (Switch-Case)

```
escolha(variável) {
    caso valor1:
        // código a ser executado se variável == valor1
    caso valor2:
        // código a ser executado se variável == valor2
    padrao:
        // código a ser executado se nenhum caso corresponder
}
```

Exemplo:

```
variavel opcao = 2;

escolha(opcao) {
    caso 1:
        escreva "Opção 1 selecionada";
    caso 2:
        escreva "Opção 2 selecionada";
    caso 3:
        escreva "Opção 3 selecionada";
    padrao:
        escreva "Opção inválida";
}
```

#### Laço Enquanto (While)

```
enquanto(condição) {
    // código a ser executado enquanto a condição for verdadeira
}
```

Exemplo:

```
variavel contador = 0;

enquanto(contador < 5) {
    escreva contador;
    contador = contador + 1;
}
```

#### Laço Para (For)

```
para(inicialização; condição; incremento) {
    // código a ser executado para cada iteração
}
```

Exemplo:

```
para(variavel i = 0; i < 5; i = i + 1) {
    escreva i;
}
```

### Funções

As funções são declaradas usando a palavra-chave `funcao`:

```
funcao nome(parâmetros) {
    // corpo da função
    retorne valor;
}
```

Exemplo:

```
funcao soma(a, b) {
    retorne a + b;
}

escreva soma(5, 3); // Imprime 8
```

### Entrada e Saída

#### Saída

Para exibir informações, use a função `escreva`:

```
escreva "Olá, mundo!";
escreva 42;
escreva "A soma é: " + soma(5, 3);
```

#### Entrada

Para ler dados do usuário, use a função `leia`:

```
escreva "Digite seu nome:";
variavel nome = leia();
escreva "Olá, " + nome + "!";
```

## Recursos Avançados

### Classes e Objetos

Jgol suporta programação orientada a objetos com classes, métodos e herança:

```
classe Pessoa {
    inicializar(nome, idade) {
        este.nome = nome;
        este.idade = idade;
    }

    apresentar() {
        escreva "Olá, meu nome é " + este.nome + " e tenho " + este.idade + " anos.";
    }
}

variavel pessoa = Pessoa();
pessoa.inicializar("João", 25);
pessoa.apresentar();
```

### Herança

Jgol suporta herança de classes usando o operador `<`:

```
classe Animal {
    emitirSom() {
        escreva "Som genérico de animal";
    }
}

classe Cachorro < Animal {
    emitirSom() {
        escreva "Au au!";
    }
}

classe Gato < Animal {
    emitirSom() {
        escreva "Miau!";
    }
}

variavel cachorro = Cachorro();
cachorro.emitirSom(); // Imprime "Au au!"

variavel gato = Gato();
gato.emitirSom(); // Imprime "Miau!"
```

### Palavra-chave Superior

Para acessar métodos da classe pai, use a palavra-chave `superior`:

```
classe Animal {
    emitirSom() {
        escreva "Som genérico de animal";
    }
}

classe Cachorro < Animal {
    emitirSom() {
        escreva "Au au!";
        superior.emitirSom(); // Chama o método da classe pai
    }
}

variavel cachorro = Cachorro();
cachorro.emitirSom(); // Imprime "Au au!" e depois "Som genérico de animal"
```

### Arrays

Jgol suporta arrays unidimensionais e multidimensionais:

```
// Array unidimensional
variavel numeros = [1, 2, 3, 4, 5];
escreva numeros[0]; // Imprime 1

// Array bidimensional (matriz)
variavel matriz = [
    [1, 2, 3],
    [4, 5, 6],
    [7, 8, 9]
];
escreva matriz[1][1]; // Imprime 5

// Array tridimensional
variavel cubo = [
    [
        [1, 2],
        [3, 4]
    ],
    [
        [5, 6],
        [7, 8]
    ]
];
escreva cubo[1][0][1]; // Imprime 6
```

## Funções Nativas

Jgol inclui algumas funções nativas:

- `leia()`: Lê uma entrada do usuário
- `escreva(valor)`: Exibe um valor na saída
- `clock()`: Retorna o tempo atual em segundos

## Exemplos Completos

### Calculadora Simples

```
escreva "Calculadora Simples";
escreva "1. Adição";
escreva "2. Subtração";
escreva "3. Multiplicação";
escreva "4. Divisão";
escreva "Escolha uma operação (1-4):";

variavel opcao = leia();
escreva "Digite o primeiro número:";
variavel num1 = leia();
escreva "Digite o segundo número:";
variavel num2 = leia();

escolha(opcao) {
    caso 1:
        escreva "Resultado: " + (num1 + num2);
    caso 2:
        escreva "Resultado: " + (num1 - num2);
    caso 3:
        escreva "Resultado: " + (num1 * num2);
    caso 4:
        se(num2 == 0) {
            escreva "Erro: Divisão por zero!";
        } senao {
            escreva "Resultado: " + (num1 / num2);
        }
    padrao:
        escreva "Opção inválida!";
}
```

### Sequência de Fibonacci

```
funcao fib(n) {
    se(n <= 1) retorne n;
    retorne fib(n - 2) + fib(n - 1);
}

escreva "Calculando a sequência de Fibonacci";
escreva "Digite um número:";
variavel num = leia();

escreva "Fibonacci de " + num + " é " + fib(num);
```

### Sistema de Gerenciamento de Alunos

```
classe Aluno {
    inicializar(nome, idade, nota) {
        este.nome = nome;
        este.idade = idade;
        este.nota = nota;
    }

    exibirInfo() {
        escreva "Nome: " + este.nome;
        escreva "Idade: " + este.idade;
        escreva "Nota: " + este.nota;
    }

    aprovado() {
        retorne este.nota >= 7;
    }
}

classe Sistema {
    inicializar() {
        este.alunos = [];
    }

    adicionarAluno(aluno) {
        este.alunos[este.alunos.length] = aluno;
    }

    listarAlunos() {
        para(variavel i = 0; i < este.alunos.length; i = i + 1) {
            escreva "Aluno " + (i + 1) + ":";
            este.alunos[i].exibirInfo();

            se(este.alunos[i].aprovado()) {
                escreva "Situação: Aprovado";
            } senao {
                escreva "Situação: Reprovado";
            }

            escreva "-------------------";
        }
    }
}

variavel sistema = Sistema();
sistema.inicializar();

variavel aluno1 = Aluno();
aluno1.inicializar("João", 20, 8.5);

variavel aluno2 = Aluno();
aluno2.inicializar("Maria", 19, 6.5);

sistema.adicionarAluno(aluno1);
sistema.adicionarAluno(aluno2);

sistema.listarAlunos();
```

## Contribuindo

Contribuições para o Jgol são bem-vindas! Se você encontrar bugs ou tiver sugestões de melhorias, por favor, abra uma issue no repositório do projeto.

## Licença

Jgol é distribuído sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.
