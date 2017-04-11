/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Champagne.Items;

/**
 *
 * @author hylkema
 */
public class Note {
  public int ID;
  public int PERIODE;
  public int ELEVE;
  public int COMP;
  public int MAT;
  public int SMAT;
  public String NOTE;

  public Note(int id, int periode, int eleve, int mat, int smat,int comp, String note) {
    this.PERIODE = periode;
    this.ELEVE = eleve;
    this.MAT = mat;
    this.SMAT = smat;
    this.COMP = comp;
    this.NOTE = note;
    this.ID = id;
  }
  
  
}
