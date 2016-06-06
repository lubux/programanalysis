    var timer_span = new Object();
    var now_date = new Date();
    var duration = this.get_duration(now_date, this.to_date);

    // If counting down and timer is completed, set timer complete statement, check for redirect, and end.
    if ( this.props['dir'] == "down" && duration.sign > 0 ) {

      // Set the timer complete statement.
      timer_span.html(this.props['timer_complete'].valueOf());

      // If there is a complete message, alert it.
      if ( this.props['tc_msg'] != '' ) {
        alert(this.props['tc_msg']);
      }

      // If there is a redirect url, redirect.
      if ( this.props['tc_redir'] != '' ) {
        window.location = this.props['tc_redir'];
      }
    }

    // Timer is not done, continue updating.
    var outhtml = new String(this.outformat);

    // try to handle counts with units first, use a try block because Drupal.formatPlural breaks javascript sometimes
    try {
      outhtml = outhtml.replace(/%years% years/,  Drupal.formatPlural(duration.years, "1 year", "@count years"));
      outhtml = outhtml.replace(/%ydays% days/,   Drupal.formatPlural(duration.days, "1 day", "@count days"));
      outhtml = outhtml.replace(/%days% days/,    Drupal.formatPlural(duration.tot_days, "1 day", "@count days"));
      outhtml = outhtml.replace(/%hours% hours/,  Drupal.formatPlural(duration.hours, "1 hour", "@count hours"));
      outhtml = outhtml.replace(/%mins% minutes/, Drupal.formatPlural(duration.minutes, "1 minute", "@count minutes"));
      outhtml = outhtml.replace(/%secs% seconds/, Drupal.formatPlural(duration.seconds, "1 second", "@count seconds"));
      outhtml = outhtml.replace(/%months% months/, Drupal.formatPlural(duration.months, "1 month", "@count months"));
      outhtml = outhtml.replace(/%tot_months% months/, Drupal.formatPlural(duration.tot_months, "1 month", "@count months"));
    }
    catch(e){
      // suppress errors
    }

    //handle counts without units
    outhtml = outhtml.replace(/%years%/, duration.years);
    outhtml = outhtml.replace(/%ydays%/, duration.days);
    outhtml = outhtml.replace(/%days%/, duration.tot_days);
    outhtml = outhtml.replace(/%hours%/, LZ(duration.hours));
    outhtml = outhtml.replace(/%mins%/, LZ(duration.minutes));
    outhtml = outhtml.replace(/%secs%/, LZ(duration.seconds));
    outhtml = outhtml.replace(/%hours_nopad%/, duration.hours);
    outhtml = outhtml.replace(/%mins_nopad%/, duration.minutes);
    outhtml = outhtml.replace(/%secs_nopad%/, duration.seconds);
    outhtml = outhtml.replace(/%sign%/, duration.sign < 0 ? '-' : '+');
    outhtml = outhtml.replace(/%months%/, duration.months);
    outhtml = outhtml.replace(/%tot_months%/, duration.tot_months);

    // Apply highlight when nearing countdown completion.
    if ( this.props['dir'] == "down" && (duration.diff <= (this.props['threshold'] * 60)) ) {
      timer_span.html('<span ' + this.props['highlight'][0] + '=' + this.props['highlight'][1] + '>' +  outhtml + '</span>');
    } else {
      timer_span._html_();
    }