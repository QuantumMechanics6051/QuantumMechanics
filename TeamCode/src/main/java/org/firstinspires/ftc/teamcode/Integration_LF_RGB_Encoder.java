package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.util.ElapsedTime;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import com.qualcomm.ftcrobotcontroller.R;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.Servo;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by student on 10/6/16.
 */
@Autonomous(name="RGB + Encoder + LF", group="RGB + Encoder")

public class Integration_LF_RGB_Encoder extends LinearOpMode{
    private ElapsedTime     runtime = new ElapsedTime();
    DcMotor LFMotor;
    DcMotor RFMotor;
    DcMotor LBMotor;
    DcMotor RBMotor;
    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 2.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED_FAST             = 0.6;
    static final double     DRIVE_SPEED_SLOW             = 0.3;
    static final double     TURN_SPEED              = 0.5;

    Servo   servo;
    ColorSensor sensorRGB1;
    ColorSensor colorSensorL;
    ColorSensor colorSensorR;// Hardware Device Object
    //ColorSensor sensorRGB2;
    DeviceInterfaceModule cdim;
    @Override
    public void runOpMode() throws InterruptedException {

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;


        double LPower = 0.2;
        double RPower = 0.2;
        double Kp = (1 - LPower) / (25);
        double errorR = 0.0;
        double errorL = 0.0;


        // get a reference to our ColorSensor object.
        colorSensorL = hardwareMap.colorSensor.get("color sensor left");
        colorSensorR = hardwareMap.colorSensor.get("color sensor right");

        colorSensorL.setI2cAddress(I2cAddr.create8bit(0x4c));

        // turn the LED on in the beginning, just so user will know that the sensor is active.

        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */

        // Send telemetry message to signify robot waiting;
        cdim = hardwareMap.deviceInterfaceModule.get("dim");


        // get a reference to our ColorSensor object.
        //sensorRGB1 = hardwareMap.colorSensor.get("color1");

        //Resetting encoders
        LFMotor = hardwareMap.dcMotor.get("LFMotor");
        LBMotor = hardwareMap.dcMotor.get("LBMotor");
        RFMotor = hardwareMap.dcMotor.get("RFMotor");
        RBMotor = hardwareMap.dcMotor.get("RBMotor");

        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        servo = hardwareMap.servo.get("buttonPusher");

        LFMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RFMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LBMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RBMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        idle();

        LFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        LBMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        RBMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Send telemetry message to indicate successful Encoder reset
        telemetry.addData("Path0", "Starting at %7d :%7d",
                RFMotor.getCurrentPosition(),
                RBMotor.getCurrentPosition(),
                RBMotor.getCurrentPosition(),
                LFMotor.getCurrentPosition());
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path,verse movement is obtained by s
        // Note: Reetting a negative distance (not speed)
        encoderDrive(DRIVE_SPEED_FAST, 9, -9, 9, -9, 5.0);  //drive forward
        encoderDrive(TURN_SPEED, 4.1, 4.1, 4.1, 4.1, 4.0);   // turn right
        while (colorSensorR.alpha() < 40) {
            telemetry.addData("color", colorSensorL.alpha());
            telemetry.addData("color", colorSensorR.alpha());
            telemetry.update();
            LFMotor.setPower(.2);
            LBMotor.setPower(.2);
            RFMotor.setPower(-.2);
            RBMotor.setPower(-.2);
            sleep(5);
            idle();
        }

//        while (colorSensorL.alpha() < 40) {
//            telemetry.addData("color", colorSensorL.alpha());
//            telemetry.addData("color", colorSensorR.alpha());
//            telemetry.update();
//            LFMotor.setPower(.2);
//            LBMotor.setPower(.2);
//            RFMotor.setPower(.2);
//            RBMotor.setPower(.2);
//            sleep(5);
//            idle();
//        }
//


        //encoderDrive(TURN_SPEED, .5,.5,.5,.5,4.0);
        //END OF ENCODER CODE




        // NOW WE SHOULD DO LINE FOLLOWING
        LFMotor.setPower(0);
        LBMotor.setPower(0);
        RFMotor.setPower(0);
        RBMotor.setPower(0);
        sleep(50);
        boolean linef = true;

        while(linef==true) {
            LPower = .2;
            RPower = .2;

            telemetry.addData("Left sensor ", colorSensorL.alpha());
            telemetry.addData("Right sensor", colorSensorR.alpha());
            telemetry.update();



            // 50 is white 0 is black
            errorR = colorSensorR.alpha() - 25;
            errorL = colorSensorL.alpha() - 25;
            telemetry.addData("ERROR: ", errorR);
            if (colorSensorR.alpha() > 30) {
               //the right sensor is seeing white it needs


                LPower = LPower * (1 + (Kp * errorR));
                telemetry.addData("LPower: ", LPower);
                RPower = RPower * (1 - (Kp * errorR));
                telemetry.addData("RPower: ", RPower);


            } else if (colorSensorR.alpha() < 20) {
                //the right sensor is seeing grey needs to go left
                telemetry.addData("LEFT", 0);
                RPower = RPower * (1 - (Kp * errorR));
                telemetry.addData("RPower: ", RPower);
                LPower = LPower * (1 + (Kp * errorR));
                telemetry.addData("LPower: ", LPower);

            }
            else {
                //both are in between 20 and 30 so both are seeing the line
                telemetry.addData("STRAIGHT", 0);
                LPower = .2;
                RPower = .2;
            }

            LFMotor.setPower(LPower);
            RFMotor.setPower(-RPower);
            RBMotor.setPower(-RPower);
            LBMotor.setPower(LPower);
            sleep(5);

            if (colorSensorL.alpha() > 30) {
                //the left sensor is seeing white so it needs to go left
                LPower = LPower * (1 + (Kp * errorL));
                telemetry.addData("LPower: ", LPower);
                RPower = RPower * (1 - (Kp * errorL));
                telemetry.addData("RPower: ", RPower);


            } else if (colorSensorL.alpha() < 20) {
                //the left sensor os seeing grey so it needs to go right
                RPower = RPower * (1 - (Kp * errorL));
                telemetry.addData("RPower: ", RPower);
                LPower = LPower * (1 + (Kp * errorL));
                telemetry.addData("LPower: ", LPower);
            }else {
                //both are in between 20 and 30 so both are seeing the line
                telemetry.addData("STRAIGHT", 0);
                LPower = .2;
                RPower = .2;
            }

            LFMotor.setPower(LPower);
            RFMotor.setPower(-RPower);
            RBMotor.setPower(-RPower);
            LBMotor.setPower(LPower);
            sleep(5);

            while(getRuntime() > 9.4){
                LFMotor.setPower(0);
                RFMotor.setPower(0);
                RBMotor.setPower(0);
                LBMotor.setPower(0);
                idle();
            }

            telemetry.update();
            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop


        }

        //
//        telemetry.addData("Path", "Complete");
//        telemetry.update();
//        int red = 0;
//        int blue = 0;
//
//        // while(opModeIsActive()) {
//        telemetry.addData("Clear 1", sensorRGB1.alpha());
//        telemetry.addData("Red  1", sensorRGB1.red());
//        telemetry.addData("Green 1", sensorRGB1.green());
//        telemetry.addData("Blue 1", sensorRGB1.blue());
//        if(sensorRGB1.red()>sensorRGB1.blue()){
//            telemetry.addData("COLOR: ", "red");
//            servo.setPosition(170);
//            sleep(100);
//
//            // red++;
//        }
//        else {
//            telemetry.addData("COLOR: ", "blue");
//            servo.setPosition(-170);
//            sleep(100);
//            // blue++;
//        }
//        telemetry.update();
//        idle();
        // }
//        if(red > blue){
//            servo.setPosition(170);
//        }
//        if(blue >  red){
//            servo.setPosition(170);
//        }
//        while(opModeIsActive()){
//            telemetry.addData("Red: ", red);
//            telemetry.addData("Blue: ", blue);
//        }
//        idle();

    }


    public void encoderDrive(double speed,
                             double LFInches, double RFInches, double LBInches, double RBInches,
                             double timeoutS) throws InterruptedException {

        int newLFTarget;
        int newRFTarget;
        int newLBTarget;
        int newRBTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLFTarget = LFMotor.getCurrentPosition() + (int)(LFInches * COUNTS_PER_INCH);
            newLBTarget = LBMotor.getCurrentPosition() + (int)(LBInches * COUNTS_PER_INCH);
            newRFTarget = RFMotor.getCurrentPosition() + (int)(RFInches * COUNTS_PER_INCH);
            newRBTarget = RBMotor.getCurrentPosition() + (int)(RBInches * COUNTS_PER_INCH);

            // newRightTarget = robot.rightMotor.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            LFMotor.setTargetPosition(newLFTarget);
            RFMotor.setTargetPosition(newRFTarget);
            LBMotor.setTargetPosition(newLBTarget);
            RBMotor.setTargetPosition(newRBTarget);

            // Turn On RUN_TO_POSITION
            LFMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            RFMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            LBMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            RBMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            LFMotor.setPower(Math.abs(speed));
            RFMotor.setPower(Math.abs(speed));
            LBMotor.setPower(Math.abs(speed));
            RBMotor.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (LFMotor.isBusy() && RFMotor.isBusy() && LBMotor.isBusy() && RBMotor.isBusy())) {

                // Display it for the driver.
//                telemetry.addData("Path1",  "Running to %7d :%7d", newLFTarget,  newRFTarget, newLBTarget, newRBTarget);
//                telemetry.addData("Path2",  "Running at %7d :%7d",
//                        LFMotor.getCurrentPosition(),
//                        RFMotor.getCurrentPosition(),
//                        LBMotor.getCurrentPosition(),
//                        RBMotor.getCurrentPosition());
                //telemetry.update();
//
//                telemetry.addData("Clear 1", sensorRGB1.alpha());
//                telemetry.addData("Red  1", sensorRGB1.red());
//                telemetry.addData("Green 1", sensorRGB1.green());
//                telemetry.addData("Blue 1", sensorRGB1.blue());
//                if(sensorRGB1.red()>sensorRGB1.blue()){
//                    telemetry.addData("COLOR: ", "red");
//                }
//                else {
//                    telemetry.addData("COLOR: ", "blue");
//                }
//                telemetry.update();

                // Allow time for other processes to run.
                idle();
            }

            // Stop all motion;
            LFMotor.setPower(0);
            RFMotor.setPower(0);
            LBMotor.setPower(0);
            RBMotor.setPower(0);

            // Turn off RUN_TO_POSITION
            LFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            RFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            LBMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            RBMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }
}