jQuery.fn.chartInit = function(config) {
	return this.each(function() {
						 this._chart = new Chart(this,config);
					 });
};

jQuery.fn.chartAdd = function(series) {
	return this.each(function() {
						 this._chart.add(series);
					 });
};

jQuery.fn.chartClear = function() {
	return this.each(function() {
						 this._chart.clear();
					 });
};

jQuery.fn.chartDraw = function(seriesLabel) {
	return this.each(function() {
						 this._chart.draw(seriesLabel);
					 });
};

