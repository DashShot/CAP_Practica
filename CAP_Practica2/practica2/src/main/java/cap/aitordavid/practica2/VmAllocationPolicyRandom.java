package cap.aitordavid.practica2;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;

import java.util.Random;

public class VmAllocationPolicyRandom extends VmAllocationPolicySimple {
    public VmAllocationPolicyRandom(List<? extends Host> list) {
        super(list);
    }

    @Override
    public boolean allocateHostForVm(Vm vm) {
        int requiredPes = vm.getNumberOfPes();
        boolean result = false;
        int tries = 0;
        List<Integer> freePesTmp = new ArrayList<Integer>();
        for (Integer freePes : getFreePes()) {
            freePesTmp.add(freePes);
        }
        if (!getVmTable().containsKey(vm.getUid())) { // if this vm was not created
            do { // we still trying until we find a host or until we try all of them
                
                // Queremos un Host aleatorio 
                int idx = (int)(Math.random()*(getVmTable().size()));
                
                Host host = getHostList().get(idx);

                Log.printLine("Intentamos Crear la VM: "+vm.getId()+"  Con el Host: "+ host.getId());
                result = host.vmCreate(vm);
                if (result) { // if vm was successfully created in the host
                    getVmTable().put(vm.getUid(), host);
                    getUsedPes().put(vm.getUid(), requiredPes);
                    getFreePes().set(idx, getFreePes().get(idx) - requiredPes);
                    Log.printLine("Máquina virtual #" + vm.getId() + " creada en el host #" + host.getId());
                    result = true;
                    break;
                } else {
                    freePesTmp.set(idx, Integer.MIN_VALUE);
                    Log.printLine("No se pudo conseguir, intentando otro aleatorio...");
                }
                tries++;
            } while (!result && tries < getFreePes().size());
        }
        
            
        return result;
    }
}