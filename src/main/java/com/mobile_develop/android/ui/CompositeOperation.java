package com.mobile_develop.android.ui;

import java.util.ArrayList;
import java.util.List;

public class CompositeOperation<ResultType, ProgressType> extends AbstractOperation<ResultType, ProgressType> {

    private List<Operation<?, ProgressType>> operations;
    private int maxStepsPerOperation;


    public CompositeOperation(boolean cancelable, int maxStepsPerOperation, List<Operation<?, ProgressType>> operations) {
        super(cancelable);
        this.maxStepsPerOperation = maxStepsPerOperation;
        this.operations = operations;
    }

    @Override
    protected ResultType doPerform() throws Exception {
        final int maxSteps = maxStepsPerOperation * operations.size();
        ArrayList<Object> results = new ArrayList<Object>(operations.size());
        fireOperationEvent(0, maxSteps, null);
        for( int i=0; i<operations.size(); i++ ) {
            Operation<?, ProgressType> operation = operations.get(i);
            final int currentIndex = i;
            fireOperationEvent(currentIndex*maxStepsPerOperation, maxSteps, null);
            Object result = operation.perform(new OperationObserver<ProgressType>() {
                @Override
                public void progressChanged(Integer value, Integer maxValue, ProgressType description) {
                    if( value != null && maxValue != null ) {
                        fireOperationEvent((value * maxStepsPerOperation)/maxValue + currentIndex*maxStepsPerOperation, maxSteps, description);
                    }
                }

                @Override
                public void cancelableChanged(boolean cancelable) {
                    setCancelable(cancelable);
                }
            });
            results.add(result);
        }
        return consume(results);
    }

    protected ResultType consume(List<?> results) {
        return null;
    }
}
