package ibm.gse.eda.stores.infra.api.dto;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class SimulationControl {
    
    public String backend;
    public int records;

    public SimulationControl(){}

    @Override
    public String toString() {
        return "Backend: " + backend + " with " + records + " records";
    }
}
