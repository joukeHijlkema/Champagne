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
public class Matiere extends Object {

  public int ID;
  public String MAT;

  public Matiere(int ID, String MAT) {
    this.ID = ID;
    this.MAT = MAT.replace("'", "''");
  }

  @Override
  public String toString() {
    return MAT;
  }

}
