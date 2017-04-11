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
public class SousMatiere extends Object {

  public int ID;
  public int MAT;
  public String SMAT;

  public SousMatiere(int ID, int MAT, String SMAT) {
    this.ID = ID;
    this.MAT = MAT;
    this.SMAT = SMAT;
  }

  @Override
  public String toString() {
    return SMAT;
  }

}
