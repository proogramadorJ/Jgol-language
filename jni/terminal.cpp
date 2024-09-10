#include <jni.h>
#include <windows.h>
#include <stdio.h>

extern "C" JNIEXPORT void JNICALL
Java_com_pedrodev_jgol_terminal_Terminal_init(JNIEnv *env, jobject obj) {
    HANDLE hInRead, hInWrite;
       HANDLE hOutRead, hOutWrite;
       SECURITY_ATTRIBUTES sa;
       PROCESS_INFORMATION pi;
       STARTUPINFO si;
       DWORD bytesWritten, bytesRead;
       char buffer[128];

       // Configura os atributos de segurança para os pipes
       sa.nLength = sizeof(SECURITY_ATTRIBUTES);
       sa.bInheritHandle = TRUE;  // Permitir herança dos pipes para o processo filho
       sa.lpSecurityDescriptor = NULL;

       // Cria pipes para redirecionar stdin e stdout
       if (!CreatePipe(&hOutRead, &hOutWrite, &sa, 0)) {
           fprintf(stderr, "Erro ao criar pipe de saída\n");
           return;
       }
       if (!CreatePipe(&hInRead, &hInWrite, &sa, 0)) {
           fprintf(stderr, "Erro ao criar pipe de entrada\n");
           return;
       }

       // Define os handles para não serem herdados pelo processo pai
       SetHandleInformation(hOutRead, HANDLE_FLAG_INHERIT, 0);
       SetHandleInformation(hInWrite, HANDLE_FLAG_INHERIT, 0);

       // Configura o STARTUPINFO para redirecionar stdin e stdout
       ZeroMemory(&si, sizeof(STARTUPINFO));
       si.cb = sizeof(STARTUPINFO);
       si.hStdInput = hInRead;
       si.hStdOutput = hOutWrite;
       si.hStdError = hOutWrite;
       si.dwFlags |= STARTF_USESTDHANDLES;

       // Cria o processo do terminal (cmd.exe) com a flag CREATE_NEW_CONSOLE
       if (!CreateProcess(NULL, "cmd.exe", NULL, NULL, TRUE, CREATE_NEW_CONSOLE, NULL, NULL, &si, &pi)) {
           fprintf(stderr, "Erro ao criar processo do terminal\n");
           return;
       }

       // Fecha os handles que não são mais necessários no processo pai
       CloseHandle(hInRead);
       CloseHandle(hOutWrite);

       // Escreve um comando no stdin do terminal
       const char *command = "echo Hello from JNI\n";
       WriteFile(hInWrite, command, strlen(command), &bytesWritten, NULL);

       // Lê a saída do terminal
       while (ReadFile(hOutRead, buffer, sizeof(buffer) - 1, &bytesRead, NULL) && bytesRead > 0) {
           buffer[bytesRead] = '\0';  // Termina a string com null
           printf("%s", buffer);  // Imprime a saída do terminal no console Java
       }

       // Espera o processo do terminal terminar
       WaitForSingleObject(pi.hProcess, INFINITE);

       // Fecha os handles restantes
       CloseHandle(hInWrite);
       CloseHandle(hOutRead);
       CloseHandle(pi.hProcess);
       CloseHandle(pi.hThread);
}