package com.example.admincollegeapp.notice;

public class PdfData {
    private String pdfTitle;
    private String pdfUrl;

    public PdfData() {
        // Default constructor required for Firebase
    }

    public PdfData(String pdfTitle, String pdfUrl) {
        this.pdfTitle = pdfTitle;
        this.pdfUrl = pdfUrl;
    }

    public String getPdfTitle() {
        return pdfTitle;
    }

    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }
}
