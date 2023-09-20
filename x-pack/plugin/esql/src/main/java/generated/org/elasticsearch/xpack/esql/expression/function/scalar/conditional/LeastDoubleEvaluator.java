// Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
// or more contributor license agreements. Licensed under the Elastic License
// 2.0; you may not use this file except in compliance with the Elastic License
// 2.0.
package org.elasticsearch.xpack.esql.expression.function.scalar.conditional;

import java.lang.IllegalArgumentException;
import java.lang.Override;
import java.lang.String;
import java.util.Arrays;
import org.elasticsearch.compute.data.Block;
import org.elasticsearch.compute.data.DoubleBlock;
import org.elasticsearch.compute.data.DoubleVector;
import org.elasticsearch.compute.data.Page;
import org.elasticsearch.compute.operator.DriverContext;
import org.elasticsearch.compute.operator.EvalOperator;
import org.elasticsearch.xpack.esql.expression.function.Warnings;
import org.elasticsearch.xpack.ql.tree.Source;

/**
 * {@link EvalOperator.ExpressionEvaluator} implementation for {@link Least}.
 * This class is generated. Do not edit it.
 */
public final class LeastDoubleEvaluator implements EvalOperator.ExpressionEvaluator {
  private final Warnings warnings;

  private final EvalOperator.ExpressionEvaluator[] values;

  private final DriverContext driverContext;

  public LeastDoubleEvaluator(Source source, EvalOperator.ExpressionEvaluator[] values,
      DriverContext driverContext) {
    this.warnings = new Warnings(source);
    this.values = values;
    this.driverContext = driverContext;
  }

  @Override
  public Block eval(Page page) {
    DoubleBlock[] valuesBlocks = new DoubleBlock[values.length];
    for (int i = 0; i < valuesBlocks.length; i++) {
      Block block = values[i].eval(page);
      if (block.areAllValuesNull()) {
        return Block.constantNullBlock(page.getPositionCount());
      }
      valuesBlocks[i] = (DoubleBlock) block;
    }
    DoubleVector[] valuesVectors = new DoubleVector[values.length];
    for (int i = 0; i < valuesBlocks.length; i++) {
      valuesVectors[i] = valuesBlocks[i].asVector();
      if (valuesVectors[i] == null) {
        return eval(page.getPositionCount(), valuesBlocks);
      }
    }
    return eval(page.getPositionCount(), valuesVectors).asBlock();
  }

  public DoubleBlock eval(int positionCount, DoubleBlock[] valuesBlocks) {
    DoubleBlock.Builder result = DoubleBlock.newBlockBuilder(positionCount);
    double[] valuesValues = new double[values.length];
    position: for (int p = 0; p < positionCount; p++) {
      for (int i = 0; i < valuesBlocks.length; i++) {
        if (valuesBlocks[i].isNull(p)) {
          result.appendNull();
          continue position;
        }
        if (valuesBlocks[i].getValueCount(p) != 1) {
          warnings.registerException(new IllegalArgumentException("single-value function encountered multi-value"));
          result.appendNull();
          continue position;
        }
      }
      // unpack valuesBlocks into valuesValues
      for (int i = 0; i < valuesBlocks.length; i++) {
        int o = valuesBlocks[i].getFirstValueIndex(p);
        valuesValues[i] = valuesBlocks[i].getDouble(o);
      }
      result.appendDouble(Least.process(valuesValues));
    }
    return result.build();
  }

  public DoubleVector eval(int positionCount, DoubleVector[] valuesVectors) {
    DoubleVector.Builder result = DoubleVector.newVectorBuilder(positionCount);
    double[] valuesValues = new double[values.length];
    position: for (int p = 0; p < positionCount; p++) {
      // unpack valuesVectors into valuesValues
      for (int i = 0; i < valuesVectors.length; i++) {
        valuesValues[i] = valuesVectors[i].getDouble(p);
      }
      result.appendDouble(Least.process(valuesValues));
    }
    return result.build();
  }

  @Override
  public String toString() {
    return "LeastDoubleEvaluator[" + "values=" + Arrays.toString(values) + "]";
  }
}
