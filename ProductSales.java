public class ProductSales implements IProduct {

    @Override
    public int TotalSales(int[][] productSales) {
        if (productSales == null) return 0;
        int sum = 0;
        for (int[] year : productSales) {
            if (year == null) continue;
            for (int v : year) sum += v;
        }
        return sum;
    }

    @Override
    public double AverageSales(int[][] productSales) {
        if (productSales == null) return 0.0;
        long sum = 0;
        int count = 0;
        for (int[] year : productSales) {
            if (year == null) continue;
            for (int v : year) {
                sum += v;
                count++;
            }
        }
        return (count == 0) ? 0.0 : (double) sum / count;
    }

    @Override
    public int MaxSale(int[][] productSales) {
        if (productSales == null) return 0;
        Integer max = null;
        for (int[] year : productSales) {
            if (year == null) continue;
            for (int v : year) {
                if (max == null || v > max) max = v;
            }
        }
        return (max == null) ? 0 : max;
    }

    @Override
    public int MinSale(int[][] productSales) {
        if (productSales == null) return 0;
        Integer min = null;
        for (int[] year : productSales) {
            if (year == null) continue;
            for (int v : year) {
                if (min == null || v < min) min = v;
            }
        }
        return (min == null) ? 0 : min;
    }
}