public class MotorInsuranceMDP {
    
    static int numStates = 4;
    static double[] premiums = {200, 400, 600, 800};
    static double accidentProbability = 0.15;
    static double discountFactor = 0.9;
    static double averageRepairCost = 500;
    
    static int getNextState(int currentState, boolean makeClaim) {
        if (makeClaim) {
            if (currentState < numStates - 1) {
                return currentState + 1;
            }
            return currentState;
        } else {
            if (currentState > 0) {
                return currentState - 1;
            }
            return currentState;
        }
    }
    
    static double calculateCost(int state, boolean makeClaim) {
        double cost = premiums[state];
        if (!makeClaim) {
            cost = cost + averageRepairCost;
        }
        return cost;
    }
    
    static double[] policyEvaluation(boolean[] policy) {
        double[] values = new double[numStates];
        
        for (int i = 0; i < 100; i++) {
            double[] newValues = new double[numStates];
            
            for (int state = 0; state < numStates; state++) {
                boolean action = policy[state];
                double cost = calculateCost(state, action);
                int nextState = getNextState(state, action);
                newValues[state] = cost + discountFactor * values[nextState];
            }
            
            values = newValues;
        }
        
        return values;
    }
    
    static boolean[] policyImprovement(double[] values) {
        boolean[] newPolicy = new boolean[numStates];
        
        for (int state = 0; state < numStates; state++) {
            double costIfClaim = calculateCost(state, true);
            int nextIfClaim = getNextState(state, true);
            double valueIfClaim = costIfClaim + discountFactor * values[nextIfClaim];
            
            double costIfNoClaim = calculateCost(state, false);
            int nextIfNoClaim = getNextState(state, false);
            double valueIfNoClaim = costIfNoClaim + discountFactor * values[nextIfNoClaim];
            
            if (valueIfClaim < valueIfNoClaim) {
                newPolicy[state] = true;
            } else {
                newPolicy[state] = false;
            }
        }
        
        return newPolicy;
    }
    
    static boolean[] policyIteration() {
        boolean[] policy = new boolean[numStates];
        for (int i = 0; i < numStates; i++) {
            policy[i] = true;
        }
        
        for (int iteration = 0; iteration < 10; iteration++) {
            double[] values = policyEvaluation(policy);
            boolean[] newPolicy = policyImprovement(values);
            
            boolean same = true;
            for (int i = 0; i < numStates; i++) {
                if (policy[i] != newPolicy[i]) {
                    same = false;
                }
            }
            
            if (same) {
                System.out.println("Converged after " + (iteration + 1) + " iterations");
                break;
            }
            
            policy = newPolicy;
        }
        
        return policy;
    }
    
    public static void main(String[] args) {
        System.out.println("Motor Insurance MDP - Policy Iteration");
        System.out.println();
        
        boolean[] optimalPolicy = policyIteration();
        
        System.out.println();
        System.out.println("Optimal Policy:");
        for (int state = 0; state < numStates; state++) {
            String decision = optimalPolicy[state] ? "CLAIM" : "DO NOT CLAIM";
            System.out.println("State " + state + ": " + decision);
        }
        
        System.out.println();
        System.out.println("Final Values (long-term cost from each state):");
        double[] finalValues = policyEvaluation(optimalPolicy);
        for (int state = 0; state < numStates; state++) {
            System.out.println("State " + state + ": $" + Math.round(finalValues[state]));
        }
    }
}