CREATE TABLE "device" (
  "_id" INTEGER NOT NULL ON CONFLICT REPLACE PRIMARY KEY AUTOINCREMENT,
  "brand" TEXT(256) NOT NULL,
  "device" TEXT(256) NOT NULL,
  "manufacturer" TEXT(256) NOT NULL,
  "model_name" TEXT(256),
  "ram" TEXT(128),
  "processor_name" TEXT(256)
);

CREATE TABLE "device_abi" (
  "device_id" INTEGER NOT NULL,
  "abi" TEXT(256) NOT NULL,
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "device_opengl" (
  "device_id" INTEGER NOT NULL,
  "opengl_version" TEXT(128) NOT NULL ON CONFLICT REPLACE,
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "device_screen_density" (
  "device_id" INTEGER NOT NULL,
  "screen_density" integer NOT NULL ON CONFLICT REPLACE,
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "device_screen_size" (
  "device_id" INTEGER NOT NULL,
  "screen_size" TEXT(256) NOT NULL ON CONFLICT REPLACE,
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE "device_sdk" (
  "device_id" INTEGER NOT NULL,
  "sdk_version" integer NOT NULL ON CONFLICT REPLACE,
  CONSTRAINT "fk_device_id" FOREIGN KEY ("device_id") REFERENCES "device" ("_id") ON DELETE CASCADE ON UPDATE CASCADE
);

