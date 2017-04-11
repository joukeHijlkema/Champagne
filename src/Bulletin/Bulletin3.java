/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bulletin;

import Champagne.Items.Eleve;
import Champagne.Items.Matiere;
import Champagne.Items.Periode;
import Champagne.dataBase;
import java.awt.Color;
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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 *
 * @author hylkema
 */
public class Bulletin3 {

  private final PDDocument document;

  //Layout
  PDFont fontPlain = PDType1Font.HELVETICA;
  PDFont fontBold = PDType1Font.HELVETICA_BOLD;
  PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
  PDFont fontMono = PDType1Font.COURIER;

  private final float topMargin = 20;
  private final float textMargin = 10;
  private final float leftMargin = 20;

  private final dataBase myDB;
  private PDPage page;
  private PDRectangle rect;
  private float left;
  private float right;
  private float width;
  private float height;
//  private float center;
//  private float leftColWidth;
//  private float midStart;
//  private float rightColWidth;
//  private float midColWidth;
//  private float midEnd;
  private float Y;
  private double textScale = 1.0;
  private final Periode P;
  private float C1;
  private float C2;
  private float C3;
  private float C4;
  private float headHeight;

  public Bulletin3(Periode p, int scale, dataBase myDB) {
    this.document = new PDDocument();
    this.myDB = myDB;
    this.P = p;
  }

  public void makePDF() {
    try {
      for (Eleve e : myDB.getElevesPerYear(this.P.ANNEE)) {
        PDPageContentStream cos = this.Head(e);
        for (Matiere m : myDB.getMatieres(P.ID)) {
        if (m.MAT.equals("Mathématiques")) {
          this.addBlock(cos, m);
        }
        if (m.MAT.equals("Français")) {
          this.addBlock(cos, m);
        }
      }
      cos.close();  
      this.document.addPage(this.page);
      }
    } catch (IOException ex) {
      Logger.getLogger(Bulletin3.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void addBlock(PDPageContentStream cos,Matiere m) {
    
  }
  public void Save(String path) {
    try {
      document.save(path);
      document.close();
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private PDPageContentStream Head(Eleve e) {
    PDPageContentStream cos = newPage();
    try {
      String img = getClass().getResource("/Icons/RFsmall.jpg").getPath();
      PDImageXObject pdImage = PDImageXObject.createFromFile(img, this.document);
      cos.drawImage(pdImage, this.left, this.Y - 139);

      float rowHeight = 21;
      float X = this.left + 100;
      float Y = this.Y - 12;

      Y -= Text(cos, "Académie", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "Département", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "Circonscription", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "École", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "Adresse", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "Téléphone", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "Courriel", fontPlain, 12, X, Y, rowHeight);

      Y = this.Y - 12;
      X += 100;
      Y -= Text(cos, "TOULOUSE", fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, "HAUTE-GARONNE", fontPlain, 12, X, Y, rowHeight);

      Y = this.Y - 12;
      X += 120;
      Y -= Text(cos, String.format("ANNEE SCOLAIRE %d/%d", this.P.ANNEE, this.P.ANNEE + 1), fontBold, 12, X + 20, Y, rowHeight);
      Y -= Text(cos, String.format("Né le %d/%d/%d", e.BY, e.BM, e.BD), fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, String.format("Élève %s %s", e.PRENOM, e.NOM), fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, String.format("Cycle/Niveau %d/%d", this.myDB.CYCLE, e.NIVEAU), fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, String.format("Classe de %s", this.myDB.CLASSE), fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, String.format("Enseignant(e)(s) %s", this.myDB.ENSEIGNANT), fontPlain, 12, X, Y, rowHeight);
      Y -= Text(cos, String.format("Nombre de bilans dans l'année scolaire: %d", 3), fontPlain, 12, X, Y, rowHeight);

      Y -= this.BoxCenteredFilled(cos,
       String.format("Bilan des acquis scolaires de l'élève : Période %d",this.P.TRIMESTRE),
       fontBold, 12, this.left, Y, this.width, rowHeight, new Color(245,245,245), rowHeight);
      
      Y -= this.BoxCenteredFilled(cos,
       String.format("Suivi des acquis scolaires de l'élève",this.P.TRIMESTRE),
       fontBold, 12, this.left, Y-5, this.width, rowHeight, new Color(245,245,245), rowHeight);
      
      Y -= 10;
      
      this.BoxCenteredFilled(cos,
       "Domaines d'enseignement",
       fontPlain, 12, this.left, Y, this.C1, this.headHeight, new Color(245,245,245), rowHeight);
      this.BoxCenteredFilled(cos,
       "Eléments du programme travaillés durant la\n période (connaissances/compétences)",
       fontPlain, 12, this.left+this.C1, Y, this.C2, this.headHeight, new Color(245,245,245), rowHeight);
      this.BoxCenteredFilled(cos,
       "Eléments du programme travaillés durant la période (connaissances/compétences)",
       fontPlain, 12, this.left+this.C1+this.C2, Y, this.C3, this.headHeight, new Color(245,245,245), rowHeight);
      
      this.Y = Y;

    } catch (IOException ex) {
      Logger.getLogger(Bulletin3.class.getName()).log(Level.SEVERE, null, ex);
    }

    return cos;
  }

  private float BoxCenteredFilled(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h, Color col, float rh) {

    try {
      cos.setNonStrokingColor(col);
      cos.fillRect(x, y - h, w, h);
      cos.stroke();
      cos.setNonStrokingColor(Color.BLACK);
      this.BoxCentered(cos, text, font, size, x, y, w, h, rh);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return h;
  }

  private float BoxCentered(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h, float rh) {
    float xt = x + (w - getTextWidth(font, text, size)) / 2;
    float yt = y - h + (h - getTextHeight(font, text, size)) / 2 + 4;
    return Box(cos, text, font, size, xt, yt, x, y, w, h, rh);
  }

  private float Box(PDPageContentStream cos, String text, PDFont font, int size, float xt, float yt, float x, float y, float w, float h, float rh) {
    try {
      cos.setLineWidth((float) 0.5);
      cos.addRect(x, y - h, w, h);
      cos.stroke();
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    Text(cos, text, font, size, xt, yt, rh);
    return h;
  }

  private float getTextWidth(PDFont font, String text, int size) {
    try {
      float w = (float) 0.0;
      for (String line : text.split("\n")) {
        System.out.println("line = "+line);
        w = max(w,font.getStringWidth(text));
      }
      return w/ 1000 * Math.round(textScale * size);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class
       .getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }

  private float getTextHeight(PDFont font, String text, int size) {
    return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * Math.round(textScale * size);
  }

  private float Text(PDPageContentStream cos, String text, PDFont font, int size, float left, float Y, float rowHeight) {
    try {
      for (String line : text.split("\n")) {
        cos.beginText();
        cos.setFont(font, Math.round(textScale * size));
        cos.moveTextPositionByAmount(left, Y);
        System.out.println("line = " + line);
        cos.drawString(line);
        Y -= rowHeight;
//                System.out.println("line = " + line);
        cos.endText();
        cos.stroke();
      }
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return rowHeight;
  }

  private PDPageContentStream newPage() {
    PDPageContentStream cos = null;
    try {
      this.page = new PDPage(PDRectangle.A4);
      this.rect = page.getMediaBox();

      this.left = leftMargin;
      this.right = rect.getWidth() - leftMargin;
      this.width = right - left;
      this.height = rect.getHeight();
      this.C1 = (float) (0.3*width);
      this.C2 = (float) (0.3*width);
      this.C3 = (float) (0.3*width);
      this.C4 = (float) (0.1*width);
      this.headHeight = (float) 100.0;

      this.Y = rect.getHeight() - topMargin;

      cos = new PDPageContentStream(document, page);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return cos;
  }
}
