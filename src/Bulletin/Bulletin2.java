/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bulletin;

import Champagne.Items.Eleve;
import Champagne.Items.Matiere;
import Champagne.Items.Note;
import Champagne.Items.Periode;
import Champagne.Items.SousMatiere;
import Champagne.dataBase;
import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 *
 * @author hylkema
 */
public class Bulletin2 {

  private final PDDocument document;

  //Layout
  PDFont fontPlain = PDType1Font.HELVETICA;
  PDFont fontBold = PDType1Font.HELVETICA_BOLD;
  PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
  PDFont fontMono = PDType1Font.COURIER;

  private final float topMargin = 20;
  private final float textMargin = 10;
  private final float leftMargin = 50;
  float rowHeight = 17;
  private final float legendeHeight = (float) (2.1 * rowHeight);
  private final float signatureHeight = (float) (7 * rowHeight);
  private final float rightColWidth = 50;
  private final float observationsHeight = 200;
  private float leftColWidth = 120;
  private final float textScale;

  private float left;
  private float right;
  private float width;
  private float height;
  private float center;
  private float midStart;
  private float midEnd;
  private float midColWidth;
  private float Y;

  private PDPage page = null;
  private PDRectangle rect = null;

  private final Color ColorLeftCol = new Color(240, 240, 240);

  private final dataBase myDB;
  private Eleve e = null;
  private Periode p = null;

  public Bulletin2(Periode p, int scale, dataBase myDB) {
    this.document = new PDDocument();
    this.p = p;
    this.textScale = (float) (1.0 + 0.01 * scale);
    this.rowHeight *= textScale;
    this.myDB = myDB;
  }

  public void makePDF() {
    boolean doTest = true;
    for (Eleve e : myDB.getElevesPerYear(this.p.ANNEE)) {
      this.e = e;
      if (doTest) {
        this.findLeftColWidth(fontBold, 14);
        doTest = false;
      }
      PDPageContentStream cos = this.newPage();
      this.addTitleBlock(cos, e);
      for (Matiere m : myDB.getMatieres(p.ID)) {
        if (m.MAT.equals("Mathématiques")) {
          this.addBlock(cos, m);
        }
        if (m.MAT.equals("Français")) {
          this.addBlock(cos, m);
        }
      }
      this.addPage(cos);
      cos = this.newPage();
      this.addTitleBlock(cos, e);
      for (Matiere m : myDB.getMatieres(p.ID)) {
        if (m.MAT.equals("Mathématiques") || m.MAT.equals("Français")) {
          continue;
        }
        this.addBlock(cos, m);
      }
      this.legende(cos, fontBold, 14);
      this.observation(cos, fontBold, 14);
      this.signature(cos, fontBold, 14);
      this.addPage(cos);
    }
  }

  public void Save(String fileName) {
    try {
      document.save(fileName);
      document.close();
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void addTitleBlock(PDPageContentStream cos, Eleve e) {

    String text = String.format("%s %s : Niveau %d - %s-%s trimestre %s",
     e.NOM,
     e.PRENOM,
     e.NIVEAU,
     this.p.ANNEE,
     this.p.ANNEE + 1,
     this.p.TRIMESTRE
    );
    System.out.println("text = " + text);
    this.Y -= Text(cos, text, fontPlain, 12, left, Y);
  }

  private void addBlock(PDPageContentStream cos, Matiere mat) {
//    this.Y -= 10;
    this.Y -= BoxCenteredFilled(cos, mat.MAT, this.fontBold, 16, left, Y, width, rowHeight + 5, Color.LIGHT_GRAY);

    for (SousMatiere smat : myDB.getSmat(mat, this.p.ID, this.e.ID)) {
      List<Note> Notes = myDB.getNotes(mat, smat, p, e);
      BoxCenteredFilled(cos, smat.SMAT, fontBold, 14, left, Y, leftColWidth, Notes.size() * this.rowHeight, ColorLeftCol);
      for (Note n : Notes) {
        System.out.println("MAT = " + mat.MAT + " SMAT = " + smat.SMAT + " COMP = " + n.COMP + " NOTE = " + n.NOTE);
        BoxLeft(cos, myDB.getComp(n.COMP).COMP, fontPlain, 11, midStart, Y, midColWidth, rowHeight);
        this.Y -= BoxLeft(cos, n.NOTE, fontPlain, 11, midEnd, Y, rightColWidth, rowHeight);
      }
    }

  }

  private PDPageContentStream newPage() {
    PDPageContentStream cos = null;
    try {
      this.page = new PDPage();
      this.rect = page.getMediaBox();

      this.left = leftMargin;
      this.right = rect.getWidth() - leftMargin;
      this.width = right - left;
      this.height = rect.getHeight();
      this.center = width / 2 + left;
      this.midStart = left + leftColWidth;
      this.midEnd = right - rightColWidth;
      this.midColWidth = width - rightColWidth - leftColWidth;

      this.Y = rect.getHeight() - topMargin;

      cos = new PDPageContentStream(document, page);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return cos;
  }

  private float Text(PDPageContentStream cos, String text, PDFont font, int size, float left, float Y) {
    try {
      for (String line : text.split("\n")) {
        cos.beginText();
        cos.setFont(fontPlain, Math.round(textScale * size));
        cos.moveTextPositionByAmount(left, Y);
        cos.drawString(line);
        Y -= this.rowHeight;
//                System.out.println("line = " + line);
        cos.endText();
        cos.stroke();
      }
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return this.rowHeight;
  }

  private void addPage(PDPageContentStream cos) {
    try {
      cos.close();
      this.document.addPage(page);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private float BoxCenteredFilled(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h, Color col) {

    try {
      cos.setNonStrokingColor(col);
      cos.fillRect(x, y - h, w, h);
      cos.stroke();
      cos.setNonStrokingColor(Color.BLACK);
      this.BoxCentered(cos, text, font, size, x, y, w, h);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    return h;
  }

  private float BoxCentered(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h) {
    float xt = x + (w - getTextWidth(font, text, size)) / 2;
    float yt = y - h + (h - getTextHeight(font, text, size)) / 2 + 4;
    return Box(cos, text, font, size, xt, yt, x, y, w, h);
  }

  private float Box(PDPageContentStream cos, String text, PDFont font, int size, float xt, float yt, float x, float y, float w, float h) {
    try {
      cos.setLineWidth((float) 0.5);
      cos.addRect(x, y - h, w, h);
      cos.stroke();
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class.getName()).log(Level.SEVERE, null, ex);
    }
    Text(cos, text, font, size, xt, yt);
    return h;
  }

  private float BoxLeft(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h) {
    float xt = x + textMargin;
    float yt = y - h + h / 2 - getTextHeight(fontPlain, text, size) / 2 + 2;
    Box(cos, text, font, size, xt, yt, x, y, w, h);
    Text(cos, text, font, size, xt, yt);
    return h;
  }

  private void BoxLeftTop(PDPageContentStream cos, String text, PDFont font, int size, float x, float y, float w, float h) {
    float xt = x + textMargin;
    float yt = y - getTextHeight(fontPlain, text, size);
    Box(cos, text, font, size, xt, yt, x, y, w, h);
    Text(cos, text, font, size, xt, yt);
  }

  private float getTextWidth(PDFont font, String text, int size) {
    try {
      return font.getStringWidth(text) / 1000 * Math.round(textScale * size);
    } catch (IOException ex) {
      Logger.getLogger(Bulletin2.class
       .getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }

  private float getTextHeight(PDFont font, String text, int size) {
    return font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * Math.round(textScale * size);
  }

  private void legende(PDPageContentStream cos, PDFont font, int size) {
    float x = left;
    float y = Y;
    float w = width;
    float h = legendeHeight;
    BoxLeftTop(cos, "1 = réussite totale",
     font, size,
     x, y, width, legendeHeight);
    x += +width / 3;
    y = y - getTextHeight(fontPlain, "1", size);
    Text(cos, "2 = bonne réussite", font, size, x, y);
    x += +width / 3;
    Text(cos, "3 = réussite partielle", font, size, x, y);
    y -= 0.9 * rowHeight;
    x = left;
    Text(cos, "4 = pas de réussite", font, size, x + textMargin, y);
    x += +width / 3;
    Text(cos, "A = absent", font, size, x, y);
    x += +width / 3;
    Text(cos, "NE = non évalué", font, size, x, y);
    this.Y -= legendeHeight;
  }

  private void observation(PDPageContentStream cos, PDFont font, int size) {
    String text = myDB.getObservation(p.ID, this.e.ID);
    float h = (text.split("\n").length + 1) * this.getTextHeight(font, text, size);
    BoxLeftTop(cos, myDB.getObservation(p.ID, this.e.ID),
     font, size,
     left, Y, width, h
    );
    this.Y -= h;
  }

  private void findLeftColWidth(PDFont font, int size) {
    for (Matiere m : myDB.getMatieres(p.ID)) {
      for (SousMatiere smat : myDB.getSmat(m, p.ID, this.e.ID)) {
        this.leftColWidth = Math.max(this.leftColWidth, getTextWidth(font, smat.SMAT, size));
      }
    }
  }

  private void signature(PDPageContentStream cos, PDFont font, int size) {
    float x = left;
    float y = this.Y;
    float w = width;
    float h = signatureHeight;
    BoxLeftTop(cos, "Signatures",
     font, size,
     x, y, width, signatureHeight);
    y -= 2.1 * getTextHeight(fontPlain, "Signatures", size);
    x = left;
    Text(cos, "Eleve :", font, size, x + textMargin, y);
    x += +width / 3;
    Text(cos, "Parents :", font, size, x, y);
    x += +width / 3;
    Text(cos, "Enseignant(e) :", font, size, x, y);
    this.Y -= signatureHeight;
  }
}
