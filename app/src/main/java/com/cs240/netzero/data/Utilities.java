package com.cs240.netzero.data;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.List;
        import java.util.stream.Collectors;
        import java.util.Locale;

public class Utilities {

    public static double getTotalSpent(List<Expense> expenses) {
        double spentThisMonth = 0.0;

        for (Expense i : expenses) {
            spentThisMonth += i.getSpent();
        }

        return spentThisMonth;
    }

    public static List<Expense> getRefuels(List<Expense> expenses) {
        List<Expense> refuels = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.getType().equals("DAILIES") || expense.getType().equals("TRAVELS")) {
                refuels.add(expense);
            }
        }
        return refuels;
    }

    public static double getEmitted(List<Expense> expenses, String fuelType, String euroCategory) {
        List<Expense> refuels = getRefuels(expenses);
        Double emitted = 0.0;

        if (refuels.isEmpty()) {
            return 0.0;
        }

        // Double percurred = refuels.get(refuels.size() - 1).getTotalKm() - refuels.get(0).getTotalKm();

        for (Expense expense : refuels) {
            double totalco2e = Double.parseDouble(expense.getDescription());
            emitted += totalco2e;
        }

        /*
        switch (fuelType) { //SOURCE: https://pdf.sciencedirectassets.com/277910/1-s2.0-S1876610219X0003X/1-s2.0-S1876610218312128/main.pdf?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEIn%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCXVzLWVhc3QtMSJGMEQCIGhxHyR05UTQxU5TZkoA%2BrCGedIzbRe6sJ2wNoMfXXPMAiBORT7uUdUuQeC5XXQtKVp12qdksjTo4EJghe3D0nC9Kiq7BQiR%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8BEAUaDDA1OTAwMzU0Njg2NSIMIkTrCRKG1B3Vgaj7Ko8FHhcvryJ%2FtHg8xmpwVUl%2FLbj6k%2FkJiRnSh3ulMzO7qmu4vrOYAZ0aLjNBcWLIMS87MSZ9%2BFAGgLZJW1CbokTjPtNrQxFuUitQr6c6fTZpMLGSDrkLR5rg%2Fx2sf4iL8OeBt3Qtxl%2F4zPfW8gfj2sQ%2B9JmiI6I0tKYqDDo1YJHL7A%2FBmLJECwW3qy%2BvdyyiSO7fLVL0je1GXJk0Q7kqbNb1Bnwqx%2BPWmZNLVaHq6%2Fw0FIH58IohU6HhWVYqrBYrPoV29IWTaW96HDemPst1xoSzWM0lKcrWpiX4kg1cugUD0Pk5tIYJ0IEpzNldqRmy4CK2i%2BeKRYLI79DdIQD5QVsj%2F6eouSwraoJoE4A0Rl1AWmas1hqctNnwe39KSWxmyiTDJQ%2FNJyPzwVLPZiS6M2yJFYVr99u0E70RrE1IZvbmERc0PlefSNaM7Wy8Cq4yOs%2BKLBxUhA9AU7wbbTGM4nXhTeTOeXfG6TpKacd5TSIh%2Byexx5phwLKJaV6oQJckqHEdD0XIA%2FVfBgfcwjvoc87CbKahatfyceT0bZwUotXWmLyfK8XC1QJvurhpl7qlD5DZyQRdB%2FCBzKLCl25pgA2v%2FI4m%2BGPE%2B2g9bL1fCaxpo09bFQuZ2Z6x6ZkXW7E4%2Bxu2OpDxOuUzmUxJCm2iNwURNjiNllvpexR%2BVTWWTzTaoXmJbGGooAAuitLqQwdozoT9uQZjFsbqE%2FI1zcUOLuVodbJ4nLYGb6OPLS3wgzFw8pVpdrGZlet9rDjaP5kqqasKequFXMEcZfm6pDIMNiXpnVOD5L%2BVhaQhUdNq9eQzQRiumGFn29cllCKH%2FUPFWGaj7ptEi9GLL5S5V30Gg%2FR0IH7%2BcIYlRa%2BmvImXO1M6VjDlpLqiBjqyAdEPt7EZE%2BfF3FQHTpKbONVo2RFe9CU7qyG%2BBoBAQJ%2FxLIFriJkR7axXxWTxkN7sZNBpsbB7YfIl2KBq8ftt%2FMZrFaC8IlXjholU3YUDLwyUEmxvnFUTRnP414LecVxPFmQ1VjaoQLLpDYq8gZE%2BTcRh2fko%2BWQxrfqjNHzlRmnDvaDBvRCvRPJUPrktwnsSZGR9iaXuOv5H0ONCks5QhA8D8UVCoLoU8Lb0i%2BRa0jn0PUw%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20230430T175411Z&X-Amz-SignedHeaders=host&X-Amz-Expires=300&X-Amz-Credential=ASIAQ3PHCVTYXHRSH3WV%2F20230430%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Signature=576ffa014734a9701f1501dc48924c01a6e3f324afa10d10f450cfdfdb7d8d2a&hash=7c14c067a16b5cfbd7d4be5f47bbaa76cc36a5d320f3a90bc4ce75cf12c15fc2&host=68042c943591013ac2b2430a89b270f6af2c76d8dfd086a07176afe7c76c2c61&pii=S1876610218312128&tid=spdf-7928b225-e82c-4d83-8e34-c4ecc7827e83&sid=ac1f37683c1c904e945917998fd499ee9a15gxrqb&type=client&tsoh=d3d3LnNjaWVuY2VkaXJlY3QuY29t&ua=1507500151530255545d&rr=7c01a60308fa878d&cc=sg
            case "petrol":
                emitted = switch (euroCategory) {
                    case "RON100" -> 1.0 * percurred;
                    case "RON98" -> 1.2 * percurred;
                    case "RON97" -> 1.5 * percurred;
                    case "RON95" -> 2.5 * percurred;
                    case "RON92" -> 3.0 * percurred;
                    default -> 2.7 * percurred;
                };
                break;
            case "diesel":
                emitted = switch (euroCategory) {
                    case "Euro 6" -> 0.5 * percurred;
                    case "Euro 5" -> 0.5 * percurred;
                    case "Euro 4" -> 0.5 * percurred;
                    case "Euro 3" -> 0.6 * percurred;
                    case "Euro 2" -> 1.0 * percurred;
                    default -> 2.7 * percurred;
                };
                break;
            default: //hybrid
                emitted = switch (euroCategory) {
                    case "Euro 6" -> 0.7 * percurred;
                    default -> 0.9 * percurred;
                };
        }
         */
        return ((emitted / 10.0) * 10.0);
    }

    public static double getAvgConsumption(List<Expense> expenses) {
        List<Expense> refuels = getRefuels(expenses);
        double totalTrees = 0.0;

        if (refuels.isEmpty()) {
            return 0.0;
        }

        // Double percurred = refuels.get(refuels.size() - 1).getTotalKm() - refuels.get(0).getTotalKm();

        for (Expense expense : refuels) {
            double trees = expense.getTotalKm();
            totalTrees += trees;
        }
        /*
        double litersUsed = 0.0;
        for (Expense r : refuels) {
            litersUsed += r.getSpent() / r.getPricePerLiter();
        }

        return ((percurred / litersUsed) * 10.0) / 10.0;
         */
        return totalTrees;
    }

    public static List<Expense> getThisMonthExpenses(List<Expense> expenses) {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String[] currentDateSplit = currentDate.split("/");

        return expenses.stream()
                .filter(e -> e.getDate().split("/")[1].equals(currentDateSplit[1]) && e.getDate().split("/")[2].equals(currentDateSplit[2]))
                .collect(Collectors.toList());

    }

    public static List<Expense> getPrevMonthExpenses(List<Expense> expenses) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String[] datePrevMonth = format.format(cal.getTime()).split("/");

        return expenses.stream()
                .filter(e -> e.getDate().split("/")[1].equals(datePrevMonth[1]) && e.getDate().split("/")[2].equals(datePrevMonth[2]))
                .collect(Collectors.toList());
    }

    public static String getThisMonthYear() {
        Calendar cal = Calendar.getInstance();

        SimpleDateFormat monthDate = new SimpleDateFormat("MMMM", Locale.getDefault());
        SimpleDateFormat monthYear = new SimpleDateFormat("yyyy", Locale.getDefault());

        String monthName = monthDate.format(cal.getTime()).replaceFirst(".", String.valueOf(Character.toUpperCase(monthDate.format(cal.getTime()).charAt(0))));
        String yearName = monthYear.format(cal.getTime());

        return monthName + " " + yearName;
    }

}