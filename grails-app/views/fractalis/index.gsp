<html>
<head>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'fractalis.css')}"/>
</head>

<body>

<div class="fjs-spinner">
    <span>Looking for patient ids...</span>
    <div class="fjs-rect1"></div>
    <div class="fjs-rect2"></div>
    <div class="fjs-rect3"></div>
    <div class="fjs-rect4"></div>
    <div class="fjs-rect5"></div>
</div>

<div class="fjs-transmart">
    <div class="fjs-block-0">
        <p>Welcome to Fractalis in tranSMART!</p>
        <p>Fractalis is a de-facto successor to SmartR, a popular plugin in tranSMART 16.2.</p>
        <p>More information: <a href="https://git-r3lab.uni.lu/Fractalis/fractalis">https://git-r3lab.uni.lu/Fractalis/fractalis</a></p>
    </div>
    <hr>
    <div class="fjs-block-1">
        <div style="text-align: center;">
            <select class="fjs-analysis-select" data-placeholder="1">
                <option selected disabled>--Choose an analysis--</option>
                <option value="correlation-analysis">Correlation Analysis</option>
                <option value="boxplot">Boxplot Analysis</option>
                <option value="pca-analysis">Principle Component Analysis</option>
            </select>
            <input type="button" value="Add" onclick="fjsService.setChart()"/>
        </div>
        <div>
            <p>
                <span style="font-weight: bold;">Step 1)</span>
                Please select what analysis you want to perform.
                You can add multiple charts in parallel and let them interact with each other!
            </p>
        </div>
    </div>
    <hr>
    <div class="fjs-block-2">
        <div class="fjs-concept-box"></div>
        <div>
            <p>
                <span style="font-weight: bold;">Step 2)</span>
                Now please drag all data you want to analyse from the tree on the left into this box here.
                This will load the selected data into the Fractalis data cache, so that subsequent analyses become really fast!
            </p>
            <input type="button" value="Clear analysis cache" style="margin-top: 1vh;" onclick="fjsService.clearCache()"/>
            <input type="button" value="Reset View" style="margin-top: 1vh;" onclick="fjsService.resetView()"/>
        </div>
    </div>
    <hr>
    <div class="fjs-block-3">
        <div>
            <p>
                <span style="font-weight: bold;">Step 3)</span>
                It will take some time to prepare the data for the first time, so this is a good opportunity to learn about the Fractalis control panel.
                Did you notice the black transparent bar that appeared on the screen? Here you can assign "roles" to the data you loaded into the cache in the first step.
                E.g. what is your X- and what is your Y-axis in a scatter plot? This panel also lets you set analysis parameters, see currently running jobs, and a couple of other things.
            </p>
        </div>
    </div>
    <hr>
    <div class="fjs-placeholders">
    </div>
</div>
</body>
</html>
