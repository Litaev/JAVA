package com.example.sb.model;


/**
 * Model class Car.
 */
public class Car {
  private String carName;
  private String carFuelType;
  private Integer carId;
  private Integer carYear;
  private Integer ownerId;
  private Integer tankVolume;
  private Integer carMileage;

  /**
   * Model class Car constructor.
   */
  public Car(String carName, String carFuelType, Integer carId, Integer carYear, Integer ownerId,
             Integer tankVolume, Integer carMileage)  {
    this.carName = carName;
    this.carFuelType = carFuelType;
    this.carId = carId;
    this.carYear = carYear;
    this.ownerId = ownerId;
    this.tankVolume = tankVolume;
    this.carMileage = carMileage;
  }

  public String getCarFuelType()  {
    return carFuelType;
  }

  public void setCarFuelType(String carFuelType)  {
    this.carFuelType = carFuelType;
  }

  public Integer getCarYear()  {
    return carYear;
  }

  public void setCarYear(Integer carYear)  {
    this.carYear = carYear;
  }

  public Integer getOwnerId()  {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId)  {
    this.ownerId = ownerId;
  }

  public Integer getTankVolume()  {
    return tankVolume;
  }

  public void setTankVolume(Integer tankVolume)  {
    this.tankVolume = tankVolume;
  }

  public Integer getCarMileage()  {
    return carMileage;
  }

  public void setCarMileage(Integer carMileage)  {
    this.carMileage = carMileage;
  }

  public String getCarName() {
    return carName;
  }

  public void setCarName(String carName) {
    this.carName = carName;
  }

  public Integer getCarId() {
    return carId;
  }

  public void setId(Integer carId) {
    this.carId = carId;
  }
}
