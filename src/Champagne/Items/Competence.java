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
public class Competence extends Object {

  public int ID;
  public int MAT;
  public int SMAT;
  public String COMP;

  public Competence(int Id, int mat, int smat, String COMP) {
    this.COMP = COMP;
    this.MAT = mat;
    this.SMAT = smat;
    this.ID = Id;
  }

  @Override
  public String toString() {
    return COMP;
  }
}
