/*6
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bulletin;

import Champagne.Items.Periode;
import Champagne.dataBase;
import java.io.IOException;
import static java.lang.Math.max;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author hylkema
 */
public class test {

    static public float getTextWidth(PDFont font, String text, int size) {
        try {
            float w = (float) 0.0;
            for (String line : text.split("#")) {
                System.out.println("line = " + line);
                w = max(w, font.getStringWidth(line));
            }
            return w / 1000 * Math.round(1 * size);
        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    static public float getTextHeight(PDFont font, String text, int size) {
        float h = getFontHeight(font, size);
        return h * text.split("#").length;
    }

    static public float getFontHeight(PDFont font, int size) {
        return size * font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000;
    }

    public static void test1() {
        dataBase myDB = new dataBase("Champagne");
        Periode P = new Periode(2, 2015, 2,"a","b","c");
        Bulletin3 b = new Bulletin3("/tmp/test.pdf", P, 1, myDB);
        b.makePDF();
        b.Save();
    }

    public static void test2() {
        try {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            PDPageContentStream cos = null;
            try {
                cos = new PDPageContentStream(document, page);
            } catch (IOException ex) {
                Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
            }

            float X = 501.4f;
            float Y = 556.0f;
//            String text = "HALLO";
//            String text = "HALLO#HOE#GAAT#HET";
//            String text = "HALLO#HOE#GAAT";
            String text = "Non atteints";
            int rh = 10;

            cos.moveTo(X, Y - 50);
            cos.lineTo(X, Y + 50);
            cos.stroke();
            cos.moveTo(X - 50, Y);
            cos.lineTo(X + 50, Y);
            cos.stroke();

            cos.beginText();
            cos.setFont(PDType1Font.HELVETICA, 6);
            float dtw = 0.5f * getTextWidth(PDType1Font.HELVETICA, text, 6);
            float dth = 0.0f * getFontHeight(PDType1Font.HELVETICA, 6);
            int nbLines = text.split("#").length;

            Y += 0.5f * (nbLines-1) * rh;

            for (String line : text.split("#")) {
                Matrix M = Matrix.getTranslateInstance(X, Y);
                M.concatenate(Matrix.getRotateInstance(Math.toRadians(0), 0, 0));
                M.concatenate(Matrix.getTranslateInstance(-dtw, -dth));
                cos.setTextMatrix(M);
                cos.showText(line);
                Y -= rh;
            }
            cos.close();
            document.addPage(page);
            document.save("/tmp/test.pdf");
            document.close();

        } catch (IOException ex) {
            Logger.getLogger(test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        test1();
        
//        test2();

    }

}
