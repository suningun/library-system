public class FineCalculator {

    private static final int FINE_PER_DAY = 1; // $1 per day

    // Calculate fine based on overdue days
    public int calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return 0;
        }
        return (int) overdueDays * FINE_PER_DAY;
    }
}

