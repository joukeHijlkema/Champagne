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
public class Eleve extends Object{
  public int ID;
  public String NOM;
  public String PRENOM;
  public int NIVEAU;
  public int ANNEE;
  public int BY = 0;
  public int BM = 0;
  public int BD = 0;

  public Eleve(int ID, String NOM, String PRENOM, int NIVEAU, int ANNEE) {
    this.ID = ID;
    this.NOM = NOM.replace("'", "''");
    this.PRENOM = PRENOM.replace("'", "''");
    this.NIVEAU = NIVEAU;
    this.ANNEE = ANNEE;
  }
  
  @Override
  public String toString() {
    return String.format("%s %s",NOM, PRENOM);
  }
  
  
}
