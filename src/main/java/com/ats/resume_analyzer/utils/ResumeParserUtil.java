package com.ats.resume_analyzer.utils;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ResumeParserUtil {

    public static String extractText(MultipartFile file) throws IOException {

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new RuntimeException("Invalid file type");
        }

        if (contentType.equals("application/pdf")) {
            return extractFromPdf(file);
        }

        if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return extractFromDocx(file);
        }

        throw new RuntimeException("Only PDF and DOCX files are allowed");
    }

    private static String extractFromPdf(MultipartFile file) throws IOException {

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            return cleanText(text);
        }
    }

    private static String extractFromDocx(MultipartFile file) throws IOException {

        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            String text = document.getParagraphs()
                    .stream()
                    .map(p -> p.getText())
                    .reduce("", (a, b) -> a + "\n" + b);

            return cleanText(text);
        }
    }

    private static String cleanText(String text) {

        if (text == null) return "";

        return text
                .replaceAll("\\s+", " ")  // remove extra spaces
                .trim()
                .toLowerCase();
    }
}