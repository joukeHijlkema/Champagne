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
public class Periode extends Object{

  public int ANNEE;
  public int TRIMESTRE;
  public int ID;

  public Periode(int Id, int Annee, int Trimestre) {
    this.ANNEE = Annee;
    this.TRIMESTRE = Trimestre;
    this.ID = Id;
  }

  @Override
  public String toString() {
    return String.format("%d - trimestre %d", ANNEE,TRIMESTRE);
  }
  
}
