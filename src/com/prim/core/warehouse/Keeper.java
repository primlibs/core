/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prim.core.warehouse;

import com.prim.core.AbstractApplication;
import com.prim.core.warehouse.controllerStructure.ControllerKeeper;
import com.prim.core.warehouse.modelKeeper.ModelStructureKeeper;
import com.prim.core.warehouse.pair.PairKeeper;

/**
 *
 * @author User
 */
public class Keeper {

  private PairKeeper pairKeeper;
  private ControllerKeeper controllerKeeper;
  private ModelStructureKeeper modelStructureKeeper;
  private OptionsKeeper optionsKeeper;
  private String osPath;

  public Keeper(String osPath) throws Exception {
    this.osPath = osPath;
  }

  public OptionsKeeper getOptionKeeper() throws Exception {
    if (optionsKeeper == null) {
      optionsKeeper = OptionsKeeper.getInstance(osPath);
    }
    return optionsKeeper;
  }

  public PairKeeper setPairKeeper(AbstractApplication app) {
    if (pairKeeper == null) {
      pairKeeper = PairKeeper.getInstance(app);
    } else {
      pairKeeper.setApp(app);
    }
    return pairKeeper;
  }

  public ControllerKeeper setControllerKeeper(AbstractApplication app) throws Exception {
    if (controllerKeeper == null) {
      controllerKeeper = ControllerKeeper.getInstance(app);
    } else {
      controllerKeeper.setApp(app);
    }
    return controllerKeeper;
  }

  public ModelStructureKeeper setModelStructureKeeper(AbstractApplication app) throws Exception {
    if (modelStructureKeeper == null) {
      modelStructureKeeper = ModelStructureKeeper.getInstance(app);
    } else {
      modelStructureKeeper.setApp(app);
    }
    return modelStructureKeeper;
  }

  public PairKeeper getPairKeeper() {
    return pairKeeper;
  }

  public ControllerKeeper getControllerKeeper() throws Exception {
    return controllerKeeper;
  }

  public ModelStructureKeeper getModelStructureKeeper() throws Exception {
    return modelStructureKeeper;
  }
}
