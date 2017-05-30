package sim;

import android.content.Context;
import android.util.Log;
import nl.saxion.ami.moses.healthstatusmonitoring.HealthStatusMonitoringComponent;
import nl.saxion.ami.moses.healthstatusmonitoring.informationManagement.AnalysisSystem;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.systemStatus.BioHarnessBatteryLevelModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.systemStatus.StillReceivingZephyrDataModule;
import nl.saxion.ami.moses.healthstatusmonitoring.modules.analysis.systemStatus.ZephyrCommunicationModule;
import nl.saxion.ami.moses.healthstatusmonitoring.util.Clock;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.FixedScenarioSimulator;
import nl.saxion.ami.moses.manschaphealthdemo.simulator.ReplayFileScenario;

/**
 * @author Etto Salomons
 *         created on 17/05/17.
 */


public class SimProg {
    private SimClock clock;

    public static void main(String[] args) {
        if (args.length > 0) {
            new SimProg().run(Integer.parseInt(args[0]));
        } else {
            new SimProg().run();

        }
    }

    private void run(int nr){
        this.clock = new SimClock();
        Clock.setClock(clock);

        HealthStatusMonitoringComponent hsmc = new HealthStatusMonitoringComponent();
        Context context = new Context();
        hsmc.start(context);
        unregisterZephyrModules();

        ReplayFileScenario sim = ReplayFileScenario.getInstance();
        String filename = getFilename(nr);
        sim.readFile(filename);
        sim.startSimulator();
    }

    private void run() {
        this.clock = new SimClock();
        Clock.setClock(clock);

        HealthStatusMonitoringComponent hsmc = new HealthStatusMonitoringComponent();
        Context context = new Context();
        hsmc.start(context);
        unregisterZephyrModules();

//        FixedScenarioSimulator sim = FixedScenarioSimulator.getInstance();
//        sim.createScenarioIndicator4();
//        sim.startSimulator();

        ReplayFileScenario sim = ReplayFileScenario.getInstance();
        String filename = getFilename(18);
        sim.readFile(filename);
        sim.startSimulator();
    }

    private void unregisterZephyrModules() {
        AnalysisSystem as = AnalysisSystem.getInstance();
        as.unregisterModule(BioHarnessBatteryLevelModule.class);
        as.unregisterModule(ZephyrCommunicationModule.class);
        as.unregisterModule(StillReceivingZephyrDataModule.class);
    }

    private String getFilename(int nr){
        String[] files = {
                "Hilco combi hardl-thuiswerkzaamheden 2.40u hardl 8k in 40min daarna 2u lichte werkzaamheden thuis.csv" ,
                "Hilco combi hardl-thuiswerkzaamheden 5u hardl 8k interval in 50min daarna 4u rustig thuis.csv" ,
                "Hilco fietsen 14,3k in 40min woon-werk incl herstel op eind.csv" ,
                "HIlco fietsen 14k in 31min woon-werk redelijk intensief.csv" ,
                "Hilco fietsen 14k in 40min woon-werk rustig.csv" ,
                "Hilco hardl 8k in 1.15u interval met relatief veel pauze.csv" ,
                "Hilco hardl 10,1 in 1.11u vele korte intervallen.csv" ,
                "Hilco hardl 10,1 in 43min crosswedstrijd zeer intensief extra hoge HR laatste 7min.csv" ,
                "Hilco hardl 10,1k in 40min wedstrijd zeer intensief.csv" ,
                "HIlco hardl 10,5k in 44min crosswedstrijd zeer intensief met 3xheuvel, extra hoge HR.csv" ,
                "Hilco hardl 10k in 1u bergachtig terrein lange klimmen en dalen.csv" ,
                "Hilco hardl 10k in 41 min wedstrijd zeer intensief.csv" ,
                "Hilco hardl 10k in 42min wedstrijd continu zeer hoge HR.csv" ,
                "Hilco hardl 10k in 43min westrijd intensief warm HR tussen 34 en 39 onbetrouwbaar te laag.csv" ,
                "Hilco hardl 13,6k in 1,16u intensieve interval piramide.csv" ,
                "Hilco hardl 15,3k in 1.33u veel wisselend tempo en HR. ook intensieve onderdelen.csv" ,
                "Hilco hardl 15,6k in 1.33u duurloop geaccidenteerd terrein, wisselend tempo.csv" ,
                "Hilco hardl 16,6k in 1.12min wedstrijd intensief warm, tussen min 2 en 29 HR onbetrouwbaar te laag.csv" ,
                "Hilco hardl 16k in 1.25u rustige duurloop bos.csv" ,
                "Hilco hardl 18,9k in 3.30u inlopen-langepauze-interval-langepauze-uitlopen.csv" ,
                "Hilco hardl 21,1 in 1.35u halve marathon wedstrijd, zeer intensief HR tussen 1.15 en 1.25u onbetrouwbaar te laag.csv" ,
                "Hilco hardlopen 12k in 1,1u, rustige duurloop eindigend met pauze en versnellingen.csv" ,
                "Hilco wandelen 10,5 km in 2,17u, normaal tempo incl pauze.csv" ,
                "Hilco 16k in 2u wisselend noodgedwongen rustig en herstel tussendoor door ribkneuzingen.csv"};
        return "recordings/" + files[nr];
    }
}
