package finance_management;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class processsing {

    boolean run = true;

    public void run(Scanner scanner, Connection cursor, String name) throws SQLException {

        System.out.printf("\n%s, Вы успешно авторизовались.\n\n", name);
        String user_uuid = processsing_handler.getUserUUID(cursor, name);
        processsing_handler.calculateWallet(cursor, user_uuid);

        String menu = ("""
                    \nВыберите операцию:
                    1 - Записать доход
                    2 - Записать расход
                    3 - Создать категорию и задать ее бюджет
                    4 - Выполнить расчет бюджета
                    5 - Подкрутить бюджет для категории
                    6 - СБП по имени
                    exit - Выход из аккаунта, возврат к меню авторизации
                    """);

        while (run) {

            System.out.println(menu);

            String answer = scanner.nextLine();

            if (answer.equals("1")) {
                System.out.println("Укажите категорию и значение в формате \"Еда: 300\".");
                String income_value_data = scanner.nextLine();
                if (income_value_data.contains(":")) {
                    String[] income_value = income_value_data.split(":");
                    String category = income_value[0];
                    String value = income_value[1].strip();
                    String category_uuid = processsing_handler.checkCategory(cursor, category, user_uuid);
                    if (category_uuid != null) {
                        processsing_handler.writeIncome(cursor, category_uuid, value);
                        System.out.printf("Вы успешно записали доход %s по категории %s.\n", value, category);
                    }
                    else {
                        System.out.println("Операция не удалась, такой категории не существует. Создайте ее выбрав соответствующий пункт меню.\n");
                    }
                }
                else {
                    System.out.println("Значение не распознано. Требуемый формат ввода \"Еда: 300\".");
                }
                processsing_handler.calculateWallet(cursor, user_uuid);
            }

            else if (answer.equals("2")) {
                System.out.println("Укажите категорию и значение в формате \"Еда: 300\".");
                String income_value_data = scanner.nextLine();
                if (income_value_data.contains(":")) {
                    String[] income_value = income_value_data.split(":");
                    String category = income_value[0];
                    String value = income_value[1].strip();
                    String category_uuid = processsing_handler.checkCategory(cursor, category, user_uuid);
                    if (category_uuid != null) {
                        processsing_handler.writeExpense(cursor, category_uuid, value);
                        System.out.printf("Вы успешно записали расход %s по категории %s.\n", value, category);
                    }
                    else {
                        System.out.println("Операция не удалась, такой категории не существует. Создайте ее выбрав соответствующий пункт меню.\n");
                    }
                }
                else {
                    System.out.println("Значение не распознано. Требуемый формат ввода \"Еда: 300\".");
                }
                processsing_handler.calculateWallet(cursor, user_uuid);
            }

            else if (answer.equals("3")) {
                System.out.println("Укажите имя категории и ее бюджет, например, \"Еда: 30000\".\nЕсли это категория доходов, то можно просто написать \"Бонус\" или \"Зарплата\".");
                String create_category = scanner.nextLine();
                if (create_category.contains(":")){
                    String[] create_category_with_limit = create_category.split(":");
                    String category_name = create_category_with_limit[0];
                    String limit_value = create_category_with_limit[1].strip();
                    String category_uuid = processsing_handler.checkCategory(cursor, category_name, user_uuid);
                    if (category_uuid != null) {
                        System.out.println("Операция не удалась, такая категория уже существует. Используйте ее для записи расходов и доходов.\n");
                    }
                    else {
                        processsing_handler.createCategory(cursor, user_uuid, category_name, limit_value);
                        System.out.printf("Вы успешно создали категорию %s. Теперь Вы можете записывать доходы и расходы по ней.\n", category_name);
                    }
                }
                else {
                    String category_uuid = processsing_handler.checkCategory(cursor, create_category.strip(), user_uuid);
                    if (category_uuid != null) {
                        System.out.println("Операция не удалась, такая категория уже существует. Используйте ее для записи расходов и доходов.\n");
                    }
                    else {
                        processsing_handler.createCategory(cursor, user_uuid, create_category.strip(), "-1");
                        System.out.printf("Вы успешно создали категорию %s. Теперь Вы можете записывать доходы и расходы по ней.\n", create_category.strip());
                    }
                }
            }

            else if (answer.equals("4")) {
                processsing_handler.calculateWallet(cursor, user_uuid);
                int total_income = processsing_handler.calculateIncomeTotal(cursor, user_uuid);
                int total_expense = processsing_handler.calculateExpenseTotal(cursor, user_uuid);

                List<Object[]> results_income;
                try {
                    results_income = processsing_handler.calculateIncomeTotalByCategory(cursor, user_uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (results_income != null) {
                    StringBuilder calcIncomeBuilder = new StringBuilder();
                    for (Object[] result : results_income) {
                        String category_name = (String) result[0];
                        int category_income = (Integer) result[1];

                        calcIncomeBuilder.append(String.format(
                                "  %s: %d\n",
                                category_name, category_income
                        ));
                    }
                    String calcIncome = calcIncomeBuilder.toString();
                    System.out.printf("\nОбщий доход: %d\n", total_income);
                    System.out.printf("Доходы по категориям:\n%s", calcIncome);
                }

                List<Object[]> results_expense;
                try {
                    results_expense = processsing_handler.calculateExpenseTotalByCategory(cursor, user_uuid);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (results_expense != null) {
                    StringBuilder calcExpenseBuilder = new StringBuilder();
                    for (Object[] result : results_expense) {
                        String category_name = (String) result[0];
                        int category_expense = (Integer) result[1];
                        int limit_value = (Integer) result[2];
                        if (limit_value != -1){
                            int remaining_budget = limit_value - category_expense;
                            if (remaining_budget < 0) {

                                calcExpenseBuilder.append(String.format(
                                        "  %s: %d, Оставшийся бюджет: %d (Внимание, лимиты бюджета превышены!)\n",
                                        category_name, category_expense, remaining_budget
                                ));
                            }
                            else {
                                calcExpenseBuilder.append(String.format(
                                        "  %s: %d, Оставшийся бюджет: %d\n",
                                        category_name, category_expense, remaining_budget
                                ));
                            }
                        }
                        else {
                            String remaining_budget = "Бюджет для данной категории не установлен";

                            calcExpenseBuilder.append(String.format(
                                    "  %s: %d, Оставшийся бюджет: %s\n",
                                    category_name, category_expense, remaining_budget
                            ));
                        }

                    }
                    String calcExpense = calcExpenseBuilder.toString();
                    System.out.printf("\nОбщие расходы: %d\n", total_expense);
                    System.out.printf("Бюджет по категориям:\n%s", calcExpense);

                    if(total_income < total_expense){
                        System.out.printf("\nВнимание! Ваши общие расходы превысили общие доходы: %d\n", total_income - total_expense);
                    }
                    else {
                        System.out.printf("\nВаш баланс: %d\n", total_income - total_expense);
                    }

                }
            }
            else if (answer.equals("5")) {
                System.out.println("Укажите имя категории и ее бюджет, например, \"Еда: 30000\".\nЕсли Вы хоите обрать категорию из рассчетов, то можно написать \"Бонус\" или \"Зарплата\" без указания лимитов или назначить лимит -1, например, \"Бонус: -1\".");
                String create_category = scanner.nextLine();
                if (create_category.contains(":")){
                    String[] create_category_with_limit = create_category.split(":");
                    String category_name = create_category_with_limit[0];
                    String limit_value = create_category_with_limit[1].strip();
                    String category_uuid = processsing_handler.checkCategory(cursor, category_name, user_uuid);
                    if (category_uuid != null) {
                        processsing_handler.updateCategory(cursor, user_uuid, category_name, limit_value);
                        System.out.printf("Вы успешно установили лимит для категории %s.\n", category_name);
                    }
                    else {
                        System.out.println("Операция не удалась, такой категории не существует. Используйте соответствующий пункт меню для категории.\n");
                    }
                }
                else {
                    String category_uuid = processsing_handler.checkCategory(cursor, create_category.strip(), user_uuid);
                    if (category_uuid != null) {
                        processsing_handler.updateCategory(cursor, user_uuid, create_category.strip(), "-1");
                        System.out.printf("Вы успешно установили лимит для категории %s.\n", create_category.strip());
                    }
                    else {
                        System.out.println("Операция не удалась, такой категории не существует. Используйте соответствующий пункт меню для категории.\n");
                    }
                }
            }
            else if (answer.equals("6")) {
                System.out.println("Здравствуйте, введите имя друга в нашей системе и сумму, которую хотите перевести в форме \"Иван: 10000\".\n");
                String friend_input = scanner.nextLine();
                if (friend_input.contains(":")){
                    String[] friend_input_data = friend_input.split(":");
                    String friend_name = friend_input_data[0];
                    String value = friend_input_data[1].strip();
                    try {
                        Integer.parseInt(value);
                        int result = processsing_handler.sbp(cursor, user_uuid, friend_name, value);
                        if (result == 1) {
                            System.out.println("Операция прошла успешно.\n");
                        }
                        else {
                            System.out.println("Операция завершилась неудачно, попробуйте еще раз позднее.\n");
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Извините, но формат ввода неправильный.\n");
                    }

                }
                processsing_handler.calculateWallet(cursor, user_uuid);

            }

            else if (answer.equals("exit")) {
                run = false;
            }
            else {
                System.out.println("Требуется выбрать пункт меню 1, 2 и т.д. или exit для выхода к меню авторизации.");
            }
        }
    }
}