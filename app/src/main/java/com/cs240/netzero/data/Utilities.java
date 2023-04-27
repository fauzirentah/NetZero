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
            if (expense.getType().equals("REFUEL")) {
                refuels.add(expense);
            }
        }
        return refuels;
    }

    public static double getEmitted(List<Expense> expenses, String fuelType, String euroCategory) {
        List<Expense> refuels = getRefuels(expenses);

        if (refuels.isEmpty()) {
            return 0.0;
        }

        Integer percurred = refuels.get(refuels.size() - 1).getTotalKm() - refuels.get(0).getTotalKm();

        Double emitted;
        switch (fuelType) { //SOURCE: WIKIPEDIA
            case "petrol":
                emitted = switch (euroCategory) {
                    case "RON100" -> 1.0 * percurred;
                    case "RON98" -> 1.0 * percurred;
                    case "RON97" -> 1.0 * percurred;
                    case "RON95" -> 2.3 * percurred;
                    case "RON92" -> 2.2 * percurred;
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

        return ((emitted / 10.0) * 10.0);
    }

    public static double getAvgConsumption(List<Expense> expenses) {
        List<Expense> refuels = getRefuels(expenses);

        if (refuels.isEmpty()) {
            return 0.0;
        }

        Integer percurred = refuels.get(refuels.size() - 1).getTotalKm() - refuels.get(0).getTotalKm();

        double litersUsed = 0.0;
        for (Expense r : refuels) {
            litersUsed += r.getSpent() / r.getPricePerLiter();
        }

        return ((percurred / litersUsed) * 10.0) / 10.0;
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