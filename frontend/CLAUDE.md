# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the React TypeScript frontend for the shopping live chat system. It's a Create React App project using React 19 and TypeScript 4.9.5.

## Build and Development Commands

```bash
# Install dependencies
npm install

# Start development server (http://localhost:3000)
npm start

# Build for production
npm run build

# Run tests in watch mode
npm test

# Run tests once
npm test -- --watchAll=false

# Run specific test file
npm test -- --testNamePattern="App"
```

## Project Structure

- **Entry point**: `src/index.tsx`
- **Main component**: `src/App.tsx` 
- **TypeScript config**: `tsconfig.json` with strict mode enabled
- **Testing**: React Testing Library with Jest
- **Styling**: CSS modules (App.css, index.css)

## Technology Stack

- **React 19** with TypeScript
- **Create React App 5.0.1** toolchain
- **React Testing Library** for testing
- **Web Vitals** for performance monitoring
- **ESLint** with react-app configuration

## Development Notes

- Uses strict TypeScript configuration
- No additional state management libraries (using React built-ins)
- Standard Create React App folder structure
- Currently contains boilerplate code - needs implementation for live chat functionality

## Code Style Guidelines

**Follow React and TypeScript developer conventions:**

- Use **ES6 modules** with `export`/`import` instead of CommonJS
- Prefer **arrow functions** for function expressions and methods
- Use **camelCase** for variables, functions, and file names
- Use **PascalCase** for React components and TypeScript interfaces/types
- Group related constants using **object literals** with `as const` assertion
- Prefer **functional components** with hooks over class components
- Use **TypeScript strict mode** - explicit typing when inference isn't clear
- Organize imports: React imports first, then third-party, then local imports
- Use **object destructuring** for props and state
- Keep components focused and single-responsibility