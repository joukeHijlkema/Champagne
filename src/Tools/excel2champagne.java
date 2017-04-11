/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import Champagne.Items.Competence;
import Champagne.Items.Eleve;
import Champagne.Items.Matiere;
import Champagne.Items.Note;
import Champagne.Items.Periode;
import Champagne.Items.SousMatiere;
import Champagne.dataBase;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author hylkema
 */
public class excel2champagne {

  Workbook wb;
  dataBase myDB = new dataBase("Champagne");

  public excel2champagne() {
    try {
      this.wb = new HSSFWorkbook(new FileInputStream("TestData/Donnees.xls"));

    } catch (IOException ex) {
      Logger.getLogger(excel2champagne.class.getName()).log(Level.SEVERE, null, ex);
    }

    for (int i = 0; i < wb.getNumberOfSheets(); i++) {
      Sheet s = wb.getSheetAt(i);
      String name = s.getSheetName();
      String[] items = name.split("-");
      int iPeriode = myDB.addPeriode(new Periode(0, Integer.parseInt(items[0].trim()), Integer.parseInt(items[1].trim().substring(10))));
      System.out.println("iPeriode = " + iPeriode);
      int r = 3;

      Row R = s.getRow(r);
      String v = getCellValue(R, 0);
      String nom, pnom, niv;
      while (r < s.getLastRowNum()) {
        nom = v;
        pnom = getCellValue(R, 1);
        niv = getCellValue(R, 2);
        if (niv.isEmpty()) {
          niv = "1";
        }
        myDB.addEleve(new Eleve(0, nom, pnom, Integer.parseInt(niv),2015));

        R = s.getRow(++r);
        v = getCellValue(R, 0);
      }

      Row R0 = s.getRow(0);
      Row R1 = s.getRow(1);
      Row R2 = s.getRow(2);
      int c = 3;
      String Tmp, Mat = "", SMat = "", Comp;
      int iComp, iMat = 0, iSmat = 0, iEleve;
      while (!"Fin".equals(getCellValue(R0, c + 1))) {
        Tmp = getCellValue(R0, c);
        if (!Tmp.isEmpty()) {
          Mat = Tmp;
          iMat  = myDB.addMat(new Matiere(0, Mat));
          System.out.println("Mat = " + Mat);
        }
        Tmp = getCellValue(R1, c);
        if (!Tmp.isEmpty()) {
          SMat = Tmp;
          iSmat = myDB.addSmat(new SousMatiere(0, iMat,SMat));
          System.out.println("-  SMat = " + SMat);
        }
        Comp = getCellValue(R2, c).replace("…", "...");
        System.out.println("----  Comp = " + Comp);
        iComp = myDB.addComp(new Competence(0, iMat,iSmat,Comp));
        
        for (r = 3; r < s.getLastRowNum(); r++) {
          R = s.getRow(r);
          iEleve = myDB.getEleveId(getCellValue(R, 0), getCellValue(R, 1));
          myDB.addNote(new Note(0, iPeriode, iEleve, iMat, iSmat, iComp, getCellValue(R, c)));
        }
        c++;
      }
    }
  }

  private String getCellValue(Row R, int c) {
    Cell C = R.getCell(c, Row.CREATE_NULL_AS_BLANK);
    String out = "";
    switch (C.getCellType()) {
      case Cell.CELL_TYPE_STRING:
        out = R.getCell(c, Row.CREATE_NULL_AS_BLANK).getStringCellValue().trim();
        break;
      case Cell.CELL_TYPE_NUMERIC:
        out = String.format("%d", (int) R.getCell(c, Row.CREATE_NULL_AS_BLANK).getNumericCellValue());
    }
    if (out.length() > 1) {
      out = out.substring(0, 1).toUpperCase() + out.substring(1);
    }
    return out;
  }

  public static void main(String[] args) {
    excel2champagne e2c = new excel2champagne();
  }
}
