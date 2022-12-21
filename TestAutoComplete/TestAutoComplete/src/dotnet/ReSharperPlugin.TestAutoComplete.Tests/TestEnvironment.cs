using System.Threading;
using JetBrains.Application.BuildScript.Application.Zones;
using JetBrains.ReSharper.Feature.Services;
using JetBrains.ReSharper.Psi.CSharp;
using JetBrains.ReSharper.TestFramework;
using JetBrains.TestFramework;
using JetBrains.TestFramework.Application.Zones;
using NUnit.Framework;

[assembly: Apartment(ApartmentState.STA)]

namespace ReSharperPlugin.TestAutoComplete.Tests
{
    [ZoneDefinition]
    public class TestAutoCompleteTestEnvironmentZone : ITestsEnvZone, IRequire<PsiFeatureTestZone>, IRequire<ITestAutoCompleteZone> { }

    [ZoneMarker]
    public class ZoneMarker : IRequire<ICodeEditingZone>, IRequire<ILanguageCSharpZone>, IRequire<TestAutoCompleteTestEnvironmentZone> { }

    [SetUpFixture]
    public class TestAutoCompleteTestsAssembly : ExtensionTestEnvironmentAssembly<TestAutoCompleteTestEnvironmentZone> { }
}
