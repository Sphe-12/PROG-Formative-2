
public class ProductSales2 implements IProductSales {
    private final int[][] productSales;
    private final int limit;

    public ProductSales2(int[][] productSales, int limit) {
        if (productSales == null) {
            this.productSales = new int[0][0];
        } else {
            this.productSales = new int[productSales.length][];
            for (int i = 0; i < productSales.length; i++) {
                int[] row = productSales[i];
                this.productSales[i] = (row == null) ? new int[0] : row.clone();
            }
        }
        this.limit = limit;
    }

    @Override
    public int[][] GetProductSales() {
        int[][] copy = new int[productSales.length][];
        for (int i = 0; i < productSales.length; i++) {
            copy[i] = productSales[i].clone();
        }
        return copy;
    }

    @Override
    public int GetTotalSales() {
        int sum = 0;
        for (int[] row : productSales) {
            if (row == null) continue;
            for (int v : row) sum += v;
        }
        return sum;
    }

    @Override
    public int GetSalesOverLimit() {
        int count = 0;
        for (int[] row : productSales) {
            if (row == null) continue;
            for (int v : row) if (v >= limit) count++;
        }
        return count;
    }

    @Override
    public int GetSalesUnderLimit() {
        int count = 0;
        for (int[] row : productSales) {
            if (row == null) continue;
            for (int v : row) if (v < limit) count++;
        }
        return count;
    }

    @Override
    public int GetProductsProcessed() {
        return productSales.length; // number of years processed
    }

    @Override
    public double GetAverageSales() {
        long sum = 0;
        int count = 0;
        for (int[] row : productSales) {
            if (row == null) continue;
            for (int v : row) {
                sum += v;
                count++;
            }
        }
        return (count == 0) ? 0.0 : (double) sum / count;
    }
}

