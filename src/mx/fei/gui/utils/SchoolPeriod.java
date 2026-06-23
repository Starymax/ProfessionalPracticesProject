package mx.fei.gui.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Objects;

public class SchoolPeriod {

    public static final String SEMESTER_FEB_JUL = "Febrero - Julio";
    public static final String SEMESTER_AUG_JAN = "Agosto - Enero";

    private final String semester;
    private final int year;
    private static int february = 2;
    private static int july = 7;
    private static final int august = 8;
    private static final int january = 1;

    public SchoolPeriod(String semester, int year) {
        this.semester = semester;
        this.year = year;
    }

    public static SchoolPeriod currentPeriod(LocalDate today) {
        int month = today.getMonthValue();
        int year = today.getYear();
        SchoolPeriod schoolPeriod;
        if (month >= february && month <= july) {
            schoolPeriod = new SchoolPeriod(SEMESTER_FEB_JUL, year);
        } else if (month >= august) {
            schoolPeriod = new SchoolPeriod(SEMESTER_AUG_JAN, year);
        } else {
            schoolPeriod = new SchoolPeriod(SEMESTER_AUG_JAN, year - 1);
        }
        return schoolPeriod;
    }

    public static SchoolPeriod getPeriodByDate(LocalDate startDate) {
        int month = startDate.getMonthValue();
        SchoolPeriod schoolPeriod;
        if (month == january) {
            schoolPeriod = new SchoolPeriod(SEMESTER_AUG_JAN, startDate.getYear() - 1);
        } else {
            schoolPeriod = new SchoolPeriod(month <= july ? SEMESTER_FEB_JUL : SEMESTER_AUG_JAN, startDate.getYear());
        }
        return schoolPeriod;
    }

    public SchoolPeriod getNextPeriod() {
        SchoolPeriod schoolPeriod = null;
        if (SEMESTER_FEB_JUL.equals(semester)) {
            schoolPeriod = new SchoolPeriod(SEMESTER_AUG_JAN, year);
        } else {
            schoolPeriod = new SchoolPeriod(SEMESTER_FEB_JUL, year + 1);
        }
        return schoolPeriod;
    }

    public YearMonth firstMonth() {
        return SEMESTER_FEB_JUL.equals(semester) ? YearMonth.of(year, 2) : YearMonth.of(year, 8);
    }

    public YearMonth lastMonth() {
        return SEMESTER_FEB_JUL.equals(semester) ? YearMonth.of(year, 7) : YearMonth.of(year + 1, 1);
    }

    @Override
    public String toString() {
        String semesterString;
        if (SEMESTER_FEB_JUL.equals(semester)) {
            semesterString = semester + " " + year;
        } else {
            semesterString = semester + " " + year + "-" + (year + 1);
        }
        return semesterString;
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual = false;
        if (super.equals(object)) {
            SchoolPeriod schoolPeriod = (SchoolPeriod) object;
            isEqual = Objects.equals(semester, schoolPeriod.semester)
                    && year == schoolPeriod.year
                    && Objects.equals(firstMonth(), schoolPeriod.firstMonth())
                    && Objects.equals(lastMonth(), schoolPeriod.lastMonth());
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(semester, year);
    }
}
