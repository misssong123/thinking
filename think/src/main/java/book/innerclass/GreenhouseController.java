package book.innerclass;

public class GreenhouseController {
    /**
     * Thermostat on night setting
     * Light is on
     * Light is off
     * Greenhouse Water is on
     * Greenhouse Water is off
     * Bing!
     * Thermostat on day setting
     * Bing!
     * Restarting system
     * Thermostat on night setting
     * Light is on
     * Light is off
     * Greenhouse Water is on
     * Bing!
     * Greenhouse Water is off
     * Thermostat on day setting
     * Bing!
     * Restarting system
     * Thermostat on night setting
     * Light is on
     * Light is off
     * Bing!
     * Greenhouse Water is on
     * Greenhouse Water is off
     * Terminating
     */
    public static void main(String[] args) {
        GreenhouseControls gc = new GreenhouseControls();
        gc.addEvent(gc.new Bell(900));
        Event[] eventList = {
                gc.new Thermostatnight(0),
                gc.new LightOn(200),
                gc.new LightOff(400),
                gc.new WaterOn(600),
                gc.new WaterOff(800),
                gc.new ThermostatDay(1400)
        };
        gc.addEvent(gc.new Restart(2000,eventList));
        gc.addEvent(new GreenhouseControls.Terminate(5000));
        gc.run();
    }
}
