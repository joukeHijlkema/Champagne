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
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.pdfbox.cos.COSName;
import static org.apache.pdfbox.pdmodel.font.FontFormat.TTF;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;

/**
 *
 * @author hylkema
 */
public class Bulletin3 {

//    PDFont fontPlain = PDType1Font.HELVETICA;
//    PDFont fontBold = PDType1Font.HELVETICA_BOLD;
//    PDFont fontItalic = PDType1Font.HELVETICA_OBLIQUE;
//    PDFont fontMono = PDType1Font.COURIER;
    PDType0Font fontPlain;
    PDType0Font fontBold;
    PDFont fontItalic;
    PDFont fontMono;

    Color g0 = new Color(255, 255, 255);
    Color g1 = new Color(245, 245, 245);
    Color g2 = new Color(210, 210, 210);
    Color g3 = new Color(180, 180, 180);
    Color g4 = new Color(150, 150, 150);
    Color g5 = new Color(242, 244, 245);
    Color g6 = new Color(203, 236, 255);
    Color g7 = new Color(253, 233, 217);

    private final myPdf document;
    private final dataBase myDB;
    private final Periode P;
    private float C1, C11, C12;
    private float C2;
    private float C3;
    private float C4;
    private final float headHeight;

    public Bulletin3(String Path, Periode p, int scale, dataBase myDB) {
        this.headHeight = 100;
        this.document = new myPdf(Path);
        this.myDB = myDB;
        this.P = p;
        try {
            fontPlain = PDType0Font.load(document.document, getClass().getResourceAsStream("/Resources/DejaVuSansCondensed.ttf"));
            fontBold = PDType0Font.load(document.document, getClass().getResourceAsStream("/Resources/DejaVuSansCondensed-Bold.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(Bulletin3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void makePDF() {
        for (Eleve e : myDB.getElevesPerYear(this.P.ANNEE)) {
            this.document.Y = this.Head(e);
            for (Matiere m : myDB.getMatieres(this.P.ID)) {
                if (m.MAT.equals("Mathématiques")) {
                    this.document.Y = this.addBlock(m, e);
                }
                if (m.MAT.equals("Français")) {
                    this.document.Y = this.addBlock(m, e);
                }
            }
            for (Matiere m : myDB.getMatieres(this.P.ID)) {
                if (m.MAT.equals("Mathématiques") || m.MAT.equals("Français")) {
                    continue;
                }
                this.document.Y = this.addBlock(m, e);
            }

            this.Foot(e);

            this.document.addPage();
//            break;
        }
    }

    private float Head(Eleve e) {

        this.document.newPage();

        this.C1 = 0.25f * this.document.width;
        this.C11 = 0.15f * this.C1;
        this.C12 = 0.85f * this.C1;
        this.C2 = 0.35f * this.document.width;
        this.C3 = 0.25f * this.document.width;
        this.C4 = 0.15f * this.document.width;

        this.document.addImage("/Resources/RFsmall.jpg", this.document.left, this.document.Y - 139);

        float rowHeight = 21;
        float X = this.document.left + 100;
        float Y = this.document.top - 12;

        Y -= this.document.Text("Académie", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("Département", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("Circonscription", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("École", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("Adresse", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("Téléphone", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("Courriel", fontPlain, 12, X, Y, rowHeight);

        Y = this.document.top - 12;
        X += 100;
        Y -= this.document.Text("TOULOUSE", fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text("HAUTE-GARONNE", fontPlain, 12, X, Y, rowHeight);

        Y = this.document.top - 12;
        X += 120;
        Y -= this.document.Text(String.format("ANNEE SCOLAIRE %d/%d", this.P.ANNEE, this.P.ANNEE + 1), fontBold, 12, X + 20, Y, rowHeight);
        Y -= this.document.Text(String.format("Né le %d/%d/%d", e.BY, e.BM, e.BD), fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text(String.format("Élève %s %s", e.PRENOM, e.NOM), fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text(String.format("Cycle/Niveau %d/%d", this.myDB.CYCLE, e.NIVEAU), fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text(String.format("Classe de %s", this.myDB.CLASSE), fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text(String.format("Enseignant(e)(s) %s", this.myDB.ENSEIGNANT), fontPlain, 12, X, Y, rowHeight);
        Y -= this.document.Text(String.format("Nombre de bilans dans l'année scolaire: %d", 3), fontPlain, 12, X, Y, rowHeight);

        Y -= this.document.BoxFilled(
                String.format("Bilan des acquis scolaires de l'élève : Période %d", this.P.TRIMESTRE),
                fontBold, 12, "Centered", this.document.left, Y, this.document.width, rowHeight, this.g1, 6);

        Y -= this.document.BoxFilled(
                String.format("Suivi des acquis scolaires de l'élève", this.P.TRIMESTRE),
                fontBold, 12, "Centered", this.document.left, Y - 5, this.document.width, rowHeight, this.g1, 12);

        Y -= 10;

        this.document.BoxFilled(
                "Domaines d'enseignement",
                fontPlain, 9, "Centered",
                this.document.left, Y, this.C1, this.headHeight,
                this.g1, rowHeight);
        this.document.BoxFilled(
                "Eléments du programme travaillés durant la#période (connaissances/compétences)",
                fontPlain, 9, "Centered",
                this.document.left + this.C1, Y, this.C2, this.headHeight,
                this.g1, 12);
        this.document.BoxFilled(
                "Acquisitions, progrès et#difficultés éventuelles",
                fontPlain, 9, "Centered",
                this.document.left + this.C1 + this.C2, Y, this.C3,
                this.headHeight,
                this.g1, 12);
        this.document.BoxFilled("Positionnement#Objectifs#d'aprentisage",
                fontPlain, 7, "Centered",
                this.document.left + this.C1 + this.C2 + this.C3, Y, this.C4,
                0.35f * this.headHeight,
                this.g1, 8);
        Y -= 0.35f * this.headHeight;

        this.document.textRotate(90.0f);
        this.document.BoxFilled("Non atteints",
                fontPlain, 6, "Centered",
                this.document.left + this.C1 + this.C2 + this.C3, Y,
                0.25f * this.C4, 0.65f * this.headHeight,
                this.g1, 6);
        this.document.BoxFilled("Atteints partiellement",
                fontPlain, 6, "Centered",
                this.document.left + this.C1 + this.C2 + this.C3 + 0.25f * this.C4, Y, 0.25f * this.C4,
                0.65f * this.headHeight,
                this.g2, 6);
        this.document.BoxFilled("Atteints",
                fontPlain, 6, "Centered",
                this.document.left + this.C1 + this.C2 + this.C3 + 0.50f * this.C4, Y, 0.25f * this.C4,
                0.65f * this.headHeight,
                this.g3, 6);
        this.document.BoxFilled("Dépassés",
                fontPlain, 6, "Centered",
                this.document.left + this.C1 + this.C2 + this.C3 + 0.75f * this.C4, Y, 0.25f * this.C4,
                0.65f * this.headHeight,
                this.g4, 6);
        this.document.textRotate(0.0f);

        return this.document.Y = Y - 0.65f * this.headHeight;

    }

    public float addBlock(Matiere m, Eleve e) {
        //find height
        float rh = 10;
        float Hmax = Math.max(rh * myDB.getItems(m, this.P, e), this.document.getTextWidth(fontPlain, m.MAT, 6) + 5);
        float Y1 = this.document.checkSpace(Hmax, this.document.Y);
        float Y2 = Y1;
        float Y3;
        float H = 0.0f;

        String text, n1, n2, n3, n4;
        int total = 0, nbNotes = 0;
        double average = 0;

        for (SousMatiere S : myDB.getSmat(m, this.P.ID, e.ID)) {
            H = 0.0f;
            Y3 = Y2;
            nbNotes = 0;
            total = 0;
            text = "";
            System.out.println("SM = " + S.SMAT);

            for (Note N : myDB.getNotes(m, S, P, e)) {
                H += rh;
                nbNotes += 1;
                text += "- " + myDB.getComp(N.COMP).COMP + "#";
                System.out.println("Note = " + N.NOTE);

                if (N.NOTE.contains("1")) {
                    total += 1;
                } else if (N.NOTE.contains("2")) {
                    total += 2;
                } else if (N.NOTE.contains("3")) {
                    total += 3;
                } else if (N.NOTE.contains("4")) {
                    total += 3;
                }
            }
            n1 = "";
            n2 = "";
            n3 = "";
            n4 = "";
            average = total / nbNotes;
            if (average <= 1) {
                n1 = "x";
            } else if (average <= 2) {
                n2 = "x";
            } else if (average <= 3) {
                n3 = "x";
            } else {
                n4 = "x";
            }
            this.document.BoxFilled(n4, fontPlain, 6, "Left",
                    this.document.left + this.C1 + this.C2 + this.C3, Y3,
                    0.25f * this.C4, H, this.g1, rh);

            this.document.BoxFilled(n3, fontPlain, 6, "Left",
                    this.document.left + this.C1 + this.C2 + this.C3 + 0.25f * this.C4, Y3,
                    0.25f * this.C4, H, this.g2, rh);
            this.document.BoxFilled(n2, fontPlain, 6, "Left",
                    this.document.left + this.C1 + this.C2 + this.C3 + 0.5f * this.C4, Y3,
                    0.25f * this.C4, H, this.g3, rh);
            this.document.BoxFilled(n4, fontPlain, 6, "Left",
                    this.document.left + this.C1 + this.C2 + this.C3 + 0.75f * this.C4, Y3,
                    0.25f * this.C4, H, this.g4, rh);
            Y3 -= rh;
            this.document.BoxFilled(S.SMAT, fontPlain, 7, "Centered",
                    this.document.left + this.C11, Y2,
                    this.C12, H, this.g1, rh);
            this.document.BoxFilled(text, fontPlain, 6, "Left",
                    this.document.left + this.C1, Y2,
                    this.C2, H, this.g1, rh);

            Y2 -= H;
        }

        this.document.BoxFilled(
                "", fontPlain, 6, "Centered",
                this.document.left + this.C1 + this.C2, this.document.Y,
                this.C3, Hmax, this.g1, rh);

        this.document.textRotate(
                90.0f);

        this.document.BoxFilled(m.MAT, fontPlain,
                6, "Centered",
                this.document.left, this.document.Y,
                this.C11, Hmax, this.g1, rh);

        this.document.textRotate(
                0.0f);

        return this.document.Y - Hmax;
    }

    private void Foot(Eleve e) {
        float Y = this.document.Y - 20;
        float X = this.document.left;
        String txt, yn;
        Y = this.document.checkSpace(270, Y);
        Y -= 10 + this.document.BoxFilled("Bilan de l'acquisition des connaissances et compétences", fontBold, 11, "Centered",
                X, Y,
                this.document.width, 15, this.g5, 15);
        Y -= this.document.BoxFilled("Parcours éducatifs : Projet(s) mis en œuvre dans l'année", fontBold, 11, "Centered",
                X, Y,
                this.document.width, 15, this.g5, 15);

        txt = String.format("Parcours citoyen : %s", myDB.getParcour("P1", P));
        txt += String.format("#Parcours d'éducation artistique et culturelle : %s", myDB.getParcour("P2", P));
        txt += String.format("#Parcours éducatif de santé : %s", myDB.getParcour("P3", P));
        Y -= 10 + this.document.BoxFilled(txt, fontPlain, 11, "Left",
                X, Y,
                this.document.width, 5 * 15, this.g6, 15);

        Y -= this.document.BoxFilled("Modalités d'accompagnement pédagogique spécifique", fontBold, 11, "Centered",
                X, Y,
                this.document.width, 15, this.g5, 15);

        if (myDB.getAide("PAP", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt = String.format(" %s PAP [plan d'accompagnement personnalisé]", yn);
        if (myDB.getAide("RASED", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s RASED [réseau d'aides spécialisées aux élèves en difficulté]", yn);
        if (myDB.getAide("PPRE", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s PPRE [projet personnalisé de réussite éducative]", yn);
        if (myDB.getAide("ULIS", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s ULIS [unité localisée pour l'inclusion scolaire]", yn);
        if (myDB.getAide("PPS", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s PPS [projet personnalisé de scolarisation]", yn);
        if (myDB.getAide("PAI", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s PAI [projet d'accueil individualisé]", yn);
        if (myDB.getAide("UPE2A", e, P)) {
            yn = "+";
        } else {
            yn = "-";
        };
        txt += String.format("# %s UPE2A [unité pédagogique pour élèves allophones arrivants]", yn);
        Y -= 10 + this.document.BoxFilled(txt, fontPlain, 10, "Left",
                X, Y,
                this.document.width, 7 * 15, this.g0, 15);

        Y = this.document.checkSpace(175, Y);
        Y -= this.document.BoxFilled("Appréciation générale sur la progression de l'élève", fontBold, 11, "Centered",
                X, Y,
                this.document.width, 15, this.g5, 15);
        txt = this.myDB.getObservation(P, e).replace("\n", "#");
        Y -= 10 + this.document.BoxFilled(txt, fontPlain, 11, "Left",
                X, Y,
                this.document.width, 150, this.g0, 15);

        Y = this.document.checkSpace(150, Y);
        Y -= this.document.BoxFilled("Communication avec les familles", fontBold, 11, "Centered",
                X, Y,
                this.document.width, 15, this.g5, 15);
        Y -= this.document.BoxFilled("Visa des parents ou du responsable légal", fontBold, 11, "Left",
                X, Y,
                this.document.width, 15, this.g5, 15);
        Y -= 10 + this.document.BoxFilled("Pris connaissance le :#Signatures :", fontBold, 11, "TopLeft",
                X, Y,
                this.document.width, 100, this.g7, 15);

        Y -= this.document.Text("Conformément aux articles 39 et suivants de la loi n° 78-17 du 6 janvier 1978 modifiée en 2004 relative à l’informatique, aux fichiers et aux libertés, toute personne peut obtenir", fontPlain, 7, X, Y, 7);
        Y -= this.document.Text("communication et, le cas échéant, rectification ou suppression des informations la concernant, en s’adressant à son établissement scolaire.", fontPlain, 7, X, Y, 7);
    }

    public void Save() {
        document.save();
        document.close();
    }

    public void Test() {

        this.document.newPage();
//        this.document.Text("HALLO", fontBold, 20, 100, 100, 20);
        this.document.textRotate(90.0f);
        this.document.Text("HALLO", fontBold, 20, 100, 100, 20);
//        this.document.BoxFilled(
//                "HALLO",
//                fontPlain, 20,
//                100, 100, 100, 100,
//                this.g1, 12);

        this.document.addPage();
    }

}
