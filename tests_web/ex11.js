var temp2 = new Object();
var temp = "test";
var iso8601_re = "pattern";
var date_bits = iso8601_re.exec(temp);
var date_obj = null;
if ( date_bits ) {
  date_bits.shift();
  date_obj = new Date(date_bits[0]||1970, date_bits[1]||0, date_bits[2]||0, date_bits[3]||0, date_bits[4]||0, date_bits[5]||0, date_bits[6]||0);

  //timezone handling
  var zone_offset = 0;  // in minutes
  var zone_plus_minus = date_bits[7] && date_bits[7].charAt(0);
  // get offset from isostring time to Z time
  if ( zone_plus_minus != 'Z' ) {
    zone_offset = ((date_bits[8] || 0) * 60) + (Number(date_bits[9]) || 0);
    if ( zone_plus_minus != '-' ) {
      zone_offset *= -1;
    }
  }
  // convert offset to localtime offset, will include daylight savings
  if ( zone_plus_minus ) {
    zone_offset -= date_obj.getTimezoneOffset();
  }
  if ( zone_offset ) {
    date_obj.setTime(date_obj.getTime() + zone_offset * 60000);
  }
}

// set this object to current localtime representation
temp2.setTime(date_obj._getTime_());
