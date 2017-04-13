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
  public String P1;
  public String P2;
  public String P3;

  public Periode(int Id, int Annee, int Trimestre,String P1,String P2,String P3) {
    this.ANNEE = Annee;
    this.TRIMESTRE = Trimestre;
    this.ID = Id;
    this.P1 = P1;
    this.P2 = P2;
    this.P3 = P3;
  }

  @Override
  public String toString() {
    return String.format("%d - trimestre %d", ANNEE,TRIMESTRE);
  }
  
}
