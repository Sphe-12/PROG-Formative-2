public class productSalesReport {
    public static void main(String[] args) {
        // 2D array: [year][quarter] - two years, three quarters each
        double[][] sales = {
            { 300, 150, 700 }, // Year 1
            { 250, 200, 600,}  // Year 2
        };

        int years = sales.length;
        int quarters = sales[0].length;

        // Single-dimensional arrays for per-year and per-quarter averages
        double[] yearTotals = new double[years];
        double[] quarterTotals = new double[quarters];

        double totalAll = 0.0;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        int count = 0;

        // calculating totals, max, min and aggregates
        for (int y = 0; y < years; y++) {
            for (int q = 0; q < quarters; q++) {
                double value = sales[y][q];
                yearTotals[y] += value;
                quarterTotals[q] += value;
                totalAll += value;
                count++;
                if (value > max) max = value;
                if (value < min) min = value;
            }
        }

        double averageAll = (count > 0) ? totalAll / count : 0.0;

        // print report
        System.out.println("Product Sales Report - 2025");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("Total sales: %,.2f%n", totalAll);
        System.out.printf("Average sales: %,.2f%n", averageAll);
        System.out.printf("Maximum sale: %,.2f%n", max);
        System.out.printf("Minimum sale: %,.2f%n", min);
        System.out.println("-------------------------------------------------------------");
    }
}