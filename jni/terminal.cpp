#include <jni.h>
#include <windows.h>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <locale>
#include <fcntl.h>
#include <io.h>

void setUTF8Locale() {
  setlocale(LC_ALL, ".UTF8");
}

BOOL WINAPI ConsoleHandler(DWORD dwCtrlType) {
    switch (dwCtrlType) {
        case CTRL_CLOSE_EVENT:
            // Intercepta o evento de fechamento da console
            // Retornamos TRUE para indicar que o evento foi tratado
            // Isso impede que o processo seja encerrado
            std::cerr << "Evento de fechamento da console interceptado. A aplicação continuará executando." << std::endl;
            return TRUE;

        case CTRL_C_EVENT:
        case CTRL_BREAK_EVENT:
        case CTRL_LOGOFF_EVENT:
        case CTRL_SHUTDOWN_EVENT:
            // Para outros eventos, permitimos o comportamento padrão
            return FALSE;

        default:
            return FALSE;
    }
}


// TODO ao invés de salvar no arquivo os logs, devolver um status para aplicação kotlin
extern "C"
JNIEXPORT void JNICALL
Java_com_pedrodev_jgol_terminal_Terminal_init(JNIEnv * env, jobject obj) {
  std::ofstream arquivo("terminal.dll-output.txt", std::ios::out | std::ios::app);

  if (!SetConsoleCtrlHandler(ConsoleHandler, TRUE)) {
          // Falha ao registrar o manipulador
          DWORD error = GetLastError();
          std::cerr << "Falha ao registrar o manipulador de eventos da console. Código de erro: " << error << std::endl;
          return;
  }

  if (!FreeConsole()) {
    DWORD error = GetLastError();
    if (error != ERROR_INVALID_HANDLE) { // ERROR_INVALID_HANDLE significa que não havia console para liberar
      arquivo << "Falha ao liberar a console existente. Código de erro: " << error << std::endl;
    }
  }

  if (!AllocConsole()) {
    DWORD error = GetLastError();
    // Opcional: tratar o erro, por exemplo, logando
    arquivo << "Falha ao alocar um novo console. Código de erro: " << error << std::endl;
    return;
  }

  SetConsoleOutputCP(CP_UTF8);
  SetConsoleCP(CP_UTF8);

  // Obtém o handle para a saída padrão (STD_OUTPUT_HANDLE)
  HANDLE hOut = GetStdHandle(STD_OUTPUT_HANDLE);
  if (hOut == INVALID_HANDLE_VALUE) {
    arquivo << "Falha ao obter o handler para saida padrão";
    return;
  }

  HANDLE hIn = GetStdHandle(STD_INPUT_HANDLE);
  if (hIn == INVALID_HANDLE_VALUE) {
    arquivo << "Falha ao obter o handler para entrada padrão";
    return;
  }
  FILE * fpOut;
  freopen_s( & fpOut, "CONOUT$", "w", stdout);

  FILE * fpIn;
  freopen_s( & fpIn, "CONIN$", "r", stdin);

  FILE * fpErr;
  freopen_s( & fpErr, "CONOUT$", "w", stderr);

  setUTF8Locale();

  std::ios::sync_with_stdio();
  arquivo << "Alocação de console funcionou\n";
  arquivo.close();

}