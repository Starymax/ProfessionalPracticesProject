package mx.fei.logic.dto;

public class IndicatorReport {
    private int id;
    private String periodFilter;
    private String shiftFilter;
    private String genderFilter;
    private String ageFilter;
    private String sectorSocialFilter;
    private String indigenousLanguageFilter;

    public IndicatorReport(int indicatorReportId, String periodFilter, String shiftFilter, String genderFilter, String ageFilter, String sectorSocialFilter, String indigenousLanguageFilter) {
        this.id = indicatorReportId;
        this.periodFilter = periodFilter;
        this.shiftFilter = shiftFilter;
        this.genderFilter = genderFilter;
        this.ageFilter = ageFilter;
        this.sectorSocialFilter = sectorSocialFilter;
        this.indigenousLanguageFilter = indigenousLanguageFilter;
    }

    public int getIndicatorReportId() {
        return id;
    }

    public void setIndicatorReportId(int indicatorReportId) {
        this.id = indicatorReportId;
    }

    public String getPeriodFilter() {
        return periodFilter;
    }

    public void setPeriodFilter(String periodFilter) {
        this.periodFilter = periodFilter;
    }

    public String getShiftFilter() {
        return shiftFilter;
    }

    public void setShiftFilter(String shiftFilter) {
        this.shiftFilter = shiftFilter;
    }

    public String getGenderFilter() {
        return genderFilter;
    }

    public void setGenderFilter(String genderFilter) {
        this.genderFilter = genderFilter;
    }

    public String getAgeFilter() {
        return ageFilter;
    }

    public void setAgeFilter(String ageFilter) {
        this.ageFilter = ageFilter;
    }

    public String getSectorSocialFilter() {
        return sectorSocialFilter;
    }

    public void setSectorSocialFilter(String sectorSocialFilter) {
        this.sectorSocialFilter = sectorSocialFilter;
    }

    public String getIndigenousLanguageFilter() {
        return indigenousLanguageFilter;
    }

    public void setIndigenousLanguageFilter(String indigenousLanguageFilter) {
        this.indigenousLanguageFilter = indigenousLanguageFilter;
    }
}
