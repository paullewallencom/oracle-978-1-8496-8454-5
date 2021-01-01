package com.oracle.cep.sample.spatial;

import java.util.ArrayList;
import java.util.List;

import oracle.spatial.geometry.JGeometry;

public class OrdsHelper
{
  public static List<Double> getOrds(JGeometry geom)
  {
    double[] ords = geom.getOrdinatesArray();
    List<Double> coords = new ArrayList<Double>();
    for (int i = 0; i < ords.length; i++)
    {
      coords.add(ords[i]);
    }
    return coords;
  }
}
