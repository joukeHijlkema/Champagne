/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bulletin;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.max;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;

/**
 *
 * @author hylkema
 */
public class myPdf {

    //Layout
    private final float topMargin = 20;
    public final float botMargin = 20;
    private final float textMargin = 10;
    private final float leftMargin = 20;

    private PDPage page;
    private PDRectangle rect;
    public float left;
    public float top;
    public float right;
    public float width;
    public float height;
    public float Y;
    private final double textScale = 1.0;
    public float C1;
    public float C2;
    public float C3;
    public float C4;
    public float headHeight;
    private final String Path;
    public final PDDocument document;
    private PDPageContentStream cos;
    private float textAngle = (float) 0.0;

    public myPdf(String Path) {
        this.Path = Path;
        this.document = new PDDocument();
    }

    public float BoxFilled(String text, PDFont font, int size, String side,
            float x, float y, float w, float h, Color col, float rh) {
        try {
            this.cos.setNonStrokingColor(col);
            this.cos.addRect(x, y - h, w, h);
            this.cos.fill();
            this.cos.stroke();
            this.cos.setNonStrokingColor(Color.BLACK);
            this.cos.setLineWidth((float) 0.5);
            this.cos.addRect(x, y - h, w, h);
            this.cos.stroke();

//            this.cos.moveTo(x+0.4f*w, y-0.5f*h);
//            this.cos.lineTo(x+0.6f*w, y-0.5f*h);
//            this.cos.stroke();
//            this.cos.moveTo(x+0.5f*w, y-0.4f*h);
//            this.cos.lineTo(x+0.5f*w, y-0.6f*h);
//            this.cos.stroke();
            switch (side) {
                case "Centered":
                    this.TextCentered(text, font, size, x + 0.5f * w, y - 0.5f * h, rh);
                    break;
                case "Left":
                    this.TextLeft(text, font, size, x + 5, y - 0.5f * h, rh);
                    break;
                case "TopLeft":
                    this.TextLeft(text, font, size, x + 5, y - 1.5f * size, rh);
                    break;
                default:
                    System.out.println("Don't know what side " + side + " is !!");
            }

        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return h;
    }

    public float getTextWidth(PDFont font, String text, int size) {
        try {
            float w = (float) 0.0;
            for (String line : text.split("#")) {
                w = max(w, font.getStringWidth(line));
            }
            return w / 1000 * Math.round(textScale * size);
        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    private float getTextHeight(PDFont font, String text, int size) {
        float h = getFontHeight(font, size);
        return h * text.split("#").length;
    }

    public float getFontHeight(PDFont font, int size) {
        return size * font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000;
    }

    public float TextCentered(String text, PDFont font, int size, float X, float Y, float rowHeight) {
        try {
            this.cos.beginText();
            this.cos.setFont(font, Math.round(textScale * size));
            float dtw = 0.5f * getTextWidth(font, text, size);
            float dth = 0.3f * getFontHeight(font, size);
            int nbLines = text.split("#").length;
            Y += 0.5f * (nbLines - 1) * rowHeight;

            for (String line : text.split("#")) {
                if (line.length() == 0) {
                    continue;
                }
//                System.out.println("X="+X+" Y="+Y);
//                System.out.println("dtw="+dtw+" dth="+dth);
                Matrix M = Matrix.getTranslateInstance(X, Y);
                M.concatenate(Matrix.getRotateInstance(this.textAngle, 0, 0));
                M.concatenate(Matrix.getTranslateInstance(-dtw, -dth));

                this.cos.setTextMatrix(M);
                this.cos.showText(this.fix(line));
//                this.cos.showText(new String(Base64.getDecoder().decode(line), "UTF-8"));

                Y -= rowHeight;
            }
            this.cos.endText();
            this.cos.stroke();

        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rowHeight;
    }

    public float TextLeft(String text, PDFont font, int size, float X, float Y, float rowHeight) {
        try {
            this.cos.beginText();
            this.cos.setFont(font, Math.round(textScale * size));
            float dth = 0.3f * getFontHeight(font, size);
            int nbLines = text.split("#").length;
            Y += 0.5f * (nbLines - 1) * rowHeight;

            for (String line : text.split("#")) {
                if (line.length() == 0) {
                    continue;
                }
//                System.out.println("X="+X+" Y="+Y);
//                System.out.println("dtw="+dtw+" dth="+dth);
                Matrix M = Matrix.getTranslateInstance(X, Y);
                M.concatenate(Matrix.getRotateInstance(this.textAngle, 0, 0));
                M.concatenate(Matrix.getTranslateInstance(0, -dth));

                this.cos.setTextMatrix(M);
                this.cos.showText(this.fix(line));
//                this.cos.showText(new String(Base64.getDecoder().decode(line), "UTF-8"));
                Y -= rowHeight;
            }
            this.cos.endText();
            this.cos.stroke();

        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rowHeight;
    }

    public float Text(String text, PDFont font, int size, float X, float Y, float rowHeight) {
        try {
            for (String line : text.split("#")) {

                this.cos.beginText();
                Matrix M = Matrix.getTranslateInstance(X, Y);
                this.cos.setTextMatrix(M);
                this.cos.setFont(font, Math.round(textScale * size));

                this.cos.showText(this.fix(line));
//                this.cos.showText(new String(Base64.getDecoder().decode(line), "UTF-8"));

                Y -= rowHeight;
                this.cos.endText();
                this.cos.stroke();
            }
        } catch (IOException ex) {
            Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rowHeight;
    }

    public void newPage() {
        System.out.println("new page");
        this.page = new PDPage(PDRectangle.A4);
        this.rect = page.getMediaBox();
        this.left = leftMargin;
        this.right = rect.getWidth() - leftMargin;
        this.width = right - left;
        this.height = rect.getHeight();
        this.Y = rect.getHeight() - topMargin;
        this.top = this.Y;

        try {
            this.cos = new PDPageContentStream(this.document, this.page);
        } catch (IOException ex) {
            Logger.getLogger(myPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void save() {
        try {
            this.document.save(this.Path);
        } catch (IOException ex) {
            Logger.getLogger(myPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void close() {
        try {
            this.document.close();
        } catch (IOException ex) {
            Logger.getLogger(myPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void addImage(String resource, float X, float Y) {
        try {
            PDImageXObject pdImage = JPEGFactory.createFromStream(this.document,getClass().getResourceAsStream(resource));
            this.cos.drawImage(pdImage, X, Y);
        } catch (IOException ex) {
            Logger.getLogger(myPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void addPage() {
        try {
            this.cos.close();
            this.document.addPage(this.page);
        } catch (IOException ex) {
            Logger.getLogger(myPdf.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void textRotate(float d) {
        this.textAngle = (float) Math.toRadians(d);
    }

    float checkSpace(float H, float Y) {
        if (Y - H < this.botMargin) {
            this.addPage();
            this.newPage();
            return this.Y;
        }
        return Y;
    }

    private String fix(String line) {
        return line;
    }
}
