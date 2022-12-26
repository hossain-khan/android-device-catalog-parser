CREATE TABLE "device" (
  "_id" INTEGER NOT NULL ON CONFLICT REPLACE PRIMARY KEY AUTOINCREMENT,
  "brand" TEXT NOT NULL,
  "device" TEXT NOT NULL,
  "manufacturer" TEXT NOT NULL,
  "model_name" TEXT NOT NULL,
  "ram" TEXT NOT NULL,
  "processor_name" TEXT NOT NULL
);

CREATE TABLE "deviceAbi" (
  "device_id" INTEGER NOT NULL,
  "abi" TEXT NOT NULL ON CONFLICT REPLACE,
  PRIMARY KEY ("device_id", "abi"),
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "deviceOpengl" (
  "device_id" INTEGER NOT NULL,
  "opengl_version" TEXT NOT NULL ON CONFLICT REPLACE,
  PRIMARY KEY ("device_id", "opengl_version"),
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "deviceScreenDensity" (
  "device_id" INTEGER NOT NULL,
  "screen_density" INTEGER NOT NULL ON CONFLICT REPLACE,
  PRIMARY KEY ("device_id", "screen_density"),
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "deviceScreenSize" (
  "device_id" INTEGER NOT NULL,
  "screen_size" TEXT NOT NULL ON CONFLICT REPLACE,
  PRIMARY KEY ("device_id", "screen_size"),
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "deviceSdk" (
  "device_id" INTEGER NOT NULL,
  "sdk_version" INTEGER NOT NULL ON CONFLICT REPLACE,
  PRIMARY KEY ("device_id", "sdk_version"),
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

