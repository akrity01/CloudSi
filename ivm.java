import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;

import java.util.*;

public class ivm {

    public static void main(String[] args) {

        try {
            // 1. Initialize CloudSim
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            CloudSim.init(numUsers, calendar, false);

            // 2. Create Datacenter
            Datacenter datacenter = createDatacenter("Datacenter_1");

            // 3. Create Broker
            DatacenterBroker broker = new DatacenterBroker("Broker");
            int brokerId = broker.getId();

            // 4. Create VMs
            List<Vm> vmList = new ArrayList<>();
            Vm vm1 = new Vm(0, brokerId, 1000, 1, 1024, 10000, 1000, "Xen", new CloudletSchedulerTimeShared());
            Vm vm2 = new Vm(1, brokerId, 1000, 1, 1024, 10000, 1000, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm1);
            vmList.add(vm2);
            broker.submitVmList(vmList);

            // 5. Create Cloudlets
            List<Cloudlet> cloudletList = new ArrayList<>();

            // Cloudlet 1 produces data
            Cloudlet cloudlet1 = new Cloudlet(0, 40000, 1, 300, 300,
                    new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet1.setUserId(brokerId);
            cloudlet1.setVmId(vm1.getId());

            // Simulate data transfer delay (in seconds)
            int dataSizeMB = 500;
            double bandwidthMBps = 100;
            double transferDelay = dataSizeMB / bandwidthMBps;

            // Cloudlet 2 receives data from Cloudlet 1
            Cloudlet cloudlet2 = new Cloudlet(1, 60000, 1, 300, 300,
                    new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull());
            cloudlet2.setUserId(brokerId);
            cloudlet2.setVmId(vm2.getId());

            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);
            broker.submitCloudletList(cloudletList);

            // 6. Start Simulation
            CloudSim.startSimulation();

            List<Cloudlet> results = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            // 7. Print results
            System.out.println("\n=== Inter-VM Data Exchange Simulation ===");
            for (Cloudlet cl : results) {
                System.out.printf("Cloudlet #%d ran on VM #%d | Start: %.2f | Finish: %.2f\n",
                        cl.getCloudletId(), cl.getVmId(), cl.getExecStartTime(), cl.getFinishTime());
            }

            // 8. Print simulated data transfer time
            System.out.printf("\n[Simulation] Estimated Data Transfer Delay (VM to VM): %.2f seconds\n", transferDelay);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to create a Datacenter
    private static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(1000))); // 1 core

        Host host = new Host(0,
                new RamProvisionerSimple(8192),
                new BwProvisionerSimple(10000),
                1_000_000,
                peList,
                new VmSchedulerTimeShared(peList));

        hostList.add(host);

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                "x86", "Linux", "Xen", hostList,
                10.0, 3.0, 0.05, 0.001, 0.0);

        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
    }
}