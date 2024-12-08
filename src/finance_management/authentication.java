package finance_management;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class authentication {

    boolean run = true;

    public void run(Scanner scanner, Connection cursor) throws SQLException {
        while (this.run) {
            System.out.println("""
                    Вас приветствует программа управления личными финансами
                    У Вас уже есть учетная запись?
                    1 - Да
                    2 - Нет
                    exit - Выход из программы""");
            String answer = scanner.nextLine();

            if (answer.equals("1")) {
                System.out.println("Введите логин:\n");
                String name = scanner.nextLine();
                System.out.println("Введите пароль:\n");
                String passwd = scanner.nextLine();
                int auth_result = user_handler.getUser(cursor, name, passwd);
                if (auth_result == 1) {
                    new processsing().run(scanner, cursor, name);
                }
                System.out.println(auth_result);
            }
            else if (answer.equals("2")) {
                System.out.println("Введите логин:\n");
                String name = scanner.nextLine();
                System.out.println("Введите пароль:\n");
                String passwd = scanner.nextLine();
                System.out.printf("Вы зарегистрировались как пользователь %s. Теперь Вы можете авторизоваться выбрав 1.\n\n", name);
                user_handler.createUser(cursor, name, passwd);
            }
            else if (answer.equals("exit")) {
                run = false;
            }
            else {
                System.out.println("Требуется ввести 1, 2 или exit для выхода.");
            }
        }
    }
}
