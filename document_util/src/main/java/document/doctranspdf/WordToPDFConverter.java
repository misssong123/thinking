package document.doctranspdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WordToPDFConverter {
    public static void main(String[] args) throws IOException {
        String sourcePath = "/Users/mengsong/Downloads/document/ES表文档.docx";
        String targetPath = "/Users/mengsong/Downloads/document/out.pdf";
        simpleTrans(sourcePath,targetPath);
    }

    private static void simpleTrans(String sourcePath,String targetPath) throws IOException {
        // 加载Word文档
        XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(sourcePath)));
        // 创建一个新的PDF文档
        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, Files.newOutputStream(Paths.get(targetPath)));

        // 打开文档并写入内容
        pdfDoc.open();
        for (XWPFParagraph p : doc.getParagraphs()) {
            Paragraph para = new Paragraph();
            for (XWPFRun r : p.getRuns()) {
                para.add(r.getText(0));
            }
            pdfDoc.add(para);
        }
        // 关闭文档
        pdfDoc.close();
        doc.close();
    }
}
