package ibm.gse.eda.stores.infra.api.dto;

public class SimulationControl {
    
    public String backend;
    public int records;

    public SimulationControl(){}

    @Override
    public String toString() {
        return "Backend: " + backend + " with " + records + " records";
    }
}
