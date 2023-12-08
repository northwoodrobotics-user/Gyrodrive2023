// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.math.MathUtil;
/**
 * This is a sample program to demonstrate how to use a gyro sensor to make a robot drive straight.
 * This program uses a joystick to drive forwards and backwards while the gyro is used for direction
 * keeping.
 */
public class Robot extends TimedRobot {
    private static final double kAngleSetpoint = 0.0;
    private static final double kP = 0.02; // propotional turning 
    public CANSparkMax leftfront = new CANSparkMax(kleftfrontPort, MotorType.kBrushless);
    public CANSparkMax rightfront = new CANSparkMax(krightfrontPort, MotorType.kBrushless);
    public CANSparkMax SlaveRight = new CANSparkMax(kSlaveRightfrontPort, MotorType.kBrushless);
    public CANSparkMax SlaveLeft = new CANSparkMax(kSlaveLeftPort, MotorType.kBrushless);
    private final MotorControllerGroup m_leftGroup =
    new MotorControllerGroup(leftfront, SlaveLeft);
    private final MotorControllerGroup m_rightGroup =
    new MotorControllerGroup(rightfront, SlaveRight);
  

  // gyro calibration constant, may need to be adjusted;
  // gyro value of 360 is set to correspond to one full revolution
  private static final double kVoltsPerDegreePerSecond = 0.0128;

  private static final int kleftfrontPort = 2;
  private static final int krightfrontPort = 1;
  private static final int kSlaveLeftPort = 3;
  private static final int kSlaveRightfrontPort = 4;
  private static final SPI.Port kGyroPort = SPI.Port.kOnboardCS0;
  

 

  private final ADXRS450_Gyro m_gyro = new ADXRS450_Gyro(kGyroPort);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftGroup, m_rightGroup);
  private final XboxController m_controller = new XboxController(0);
  private final Timer m_timer = new Timer();

  @Override
  public void robotInit() {
    m_gyro.calibrate();
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_leftGroup.setInverted(true);
    SlaveRight.setInverted(false);
  }

  /**
   * The motor speed is set from the joystick while the DifferentialDrive turning value is assigned
   * from the error between the setpoint and the gyro angle.
   */
  @Override
  public void teleopPeriodic() {

    if (m_controller.getRawButton(2)){
      m_gyro.calibrate();
    }

    double turningValue = (kAngleSetpoint - m_gyro.getAngle()) * kP;
    m_robotDrive.arcadeDrive(MathUtil.applyDeadband(m_controller.getLeftY(),0.1), ((MathUtil.applyDeadband(m_controller.getRightX(), 0.1)))+ turningValue);
    
    
    SmartDashboard.putNumber("gyro direction", m_gyro.getAngle());
    SmartDashboard.putNumber("turning value", turningValue);
    SmartDashboard.putNumber("kAngleSetpoint", kAngleSetpoint);
  }
}
