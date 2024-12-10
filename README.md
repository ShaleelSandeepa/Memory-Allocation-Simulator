# Memory-Allocation-Simulator

The primary goal of this project is to create a simulation that effectively demonstrates the functionality of the Next-Fit algorithm, providing an interactive and educational tool for understanding resource management within operating systems. The application, developed using Java and Swing, features a user-friendly graphical interface that allows users to add jobs, execute them, manage the execution process, and visualize the scheduling and memory allocation in real-time.

Requirements

-- Functional requirements
•	Able to insert jobs with name and size
•	Able to execute jobs one by one and all jobs at once
•	Support to pausing, resuming and resetting the simulator
•	Should display memory block status and visualizations
•	Should display execute process steps (logs)

-- Nonfunctional requirements
•	Provide a responsive and user-friendly interface
•	Ensure low memory usage for efficient simulation

-- Assumptions and Justifications

•	Each job requires memory in a block.
•	There can be jobs that cannot allocate any memory block due to insufficient memory space in the blocks.
•	Blocks have fixed sizes initialized during startup to simplify allocation logic.

-- Functionality and Features

•	Insert Job - Adds a new job with specified name and size
•	Example jobs - Quickly populates the system with predefined jobs
•	Pause/Resume - Temporarily halts or continues the simulation
•	Reset - Restores the system to its initial state
•	Execute selected Job – simulate selected job from the jobs table
•	Execute all jobs – simulate all the jobs in order of jobs table

•	Live tracking – Display real time progress of the block
•	Display block info – show allocated jobs in each block with user friendly manner

