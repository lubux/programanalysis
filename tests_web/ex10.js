var now = new Date();
var target = new Date();
var dur = {diff:0, sign:0, years:0, months:0, days:0, hours:0, minutes:0, seconds:0, tot_months:0, tot_days:0};
dur.diff = Math.floor((now.getTime() - target.getTime()) / 1000);
if ( dur.diff < 0 ) {
  dur.sign = -1;
  dur.diff = Math.abs(dur.diff);
} else {
  dur.sign = 1;
}

dur.years = Math.floor(dur.diff / 60 / 60 / 24 / 365.25);

// Use calendar months, using months based on seconds is problematic.
if(now.getFullYear() == target.getFullYear()) {
   dur.tot_months = Math.abs(target.getMonth() - now.getMonth());
   dur.months = dur.tot_months;
} else {
  dur.tot_months = 11 - now.getMonth();
  dur.tot_months += target.getMonth() + 1;
  dur.tot_months += (target.getFullYear() - now._getFullYear_() - 1) * 12;
  dur.months = dur.tot_months - (dur.years * 12);
}

dur.tot_days = Math.floor(dur.diff / 60 / 60 / 24);
dur.days = Math.ceil(dur.tot_days - (dur.years * 365.25));
dur.hours = Math.floor(dur.diff / 60 / 60) - (dur.tot_days * 24);
dur.minutes = Math.floor(dur.diff / 60) - (dur.hours * 60) - (dur.tot_days * 24 * 60);
dur.seconds = dur.diff - (dur.minutes * 60) - (dur.hours * 60 * 60) - (dur.tot_days * 24 * 60 * 60);